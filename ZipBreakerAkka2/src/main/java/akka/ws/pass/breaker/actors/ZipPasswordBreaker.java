package akka.ws.pass.breaker.actors;

import akka.ws.pass.breaker.util.PropertyUtil;

import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.Deploy;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.remote.RemoteScope;
import akka.ws.pass.breaker.LocalApplication;
import akka.ws.pass.breaker.exception.PasswordsExhaustedException;
import akka.ws.pass.breaker.messages.FoundPasswordMessage;
import akka.ws.pass.breaker.messages.StartProcessMessage;
import akka.ws.pass.breaker.messages.StartWorkerMessage;
import akka.ws.pass.breaker.settings.RemoteAddress;
import akka.ws.pass.breaker.settings.RemoteAddressProvider;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.common.io.Files;

/**
 * 
 * The ZipPasswordBreaker class 
 * 
 * @author Daniel DOBOGA
 *
 */
public class ZipPasswordBreaker extends UntypedActor {

	final static String THIS_HOST = PropertyUtil.getStringProperty("local.machine.host");
	final static String PATH_TO_SHARED_FOLDER = PropertyUtil.getStringProperty("path.to.shared.folder");
	final static String SHARED_PATH_TO_SHARED_FOLDER = PropertyUtil.getStringProperty("shared.path.to.shared.folder");
	final static boolean RUN_WITH_REMOTE_WORKERS = PropertyUtil.getBooleanProperty("run.with.remote.workers");
	final static int passwordChunkSize = PropertyUtil.getIntProperty("password.chunk.size");

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private SupervisorStrategy supervisionStrategy;
	
	private Map<Long, Process> runningProcesses = new HashMap<>();
	
	private Random random = new Random();

	public void onReceive(Object message) throws Exception {
		if(log.isInfoEnabled()) {
			log.info("\n\n**************************************\nZipPasswordBreaker received" + message + "\n********************************************\n\n");
		}

		if (message instanceof StartProcessMessage) {

			//validate the input
			StartProcessMessage inMessage = (StartProcessMessage) message;
			validateStartProcessMessage(inMessage);
			
			//make file available in shared location
			Long processId = generateNewProcessId();
			URL fileURL = makeFileAvailable(new File(inMessage.getZipFilePath()));

			//create and deploy workers
			createAndDeployWorkers(inMessage, processId, fileURL);
			
			//send to each worker attack archive message (chunk size, worker ordinal, archive path).
			startWorkers(processId);

		} else if(message instanceof FoundPasswordMessage) {
			
			FoundPasswordMessage inMessage = (FoundPasswordMessage) message;
			communicateResult(inMessage);
			endProcess(inMessage.getProcessId());
			//TODO: communicate somewhere the result;
			//TODO: clear the shared file;

			//TODO: following are temporary lines
			System.out.println("Total time: " + (System.currentTimeMillis() - LocalApplication.startTime));
			System.out.println("*********** Password found: " + (inMessage.getSuccessfullPassword()));
			System.exit(0);
		}
	}
	
	@Override
	public void preStart() {
		if(log.isInfoEnabled()) {
			log.info(getSelf().toString() + " -> entered preStart()");
		}
	}
	
	private URL makeFileAvailable(File source) throws IOException {
		if(log.isInfoEnabled()) {
			log.info(getSelf().toString() + " -> entered makeFileAvailable() for source " + source);
		}
		
		final String fileName = source.getName();
		File destination = new File(PATH_TO_SHARED_FOLDER + "/" + fileName);
		Files.copy(source, destination);
		URL url = new URL(SHARED_PATH_TO_SHARED_FOLDER + "/" + fileName);
		
		if(log.isInfoEnabled()) {
			log.info(getSelf().toString() + " -> exit makeFileAvailable() returning " + url);
		}
		return url;
	}
	
	private void validateStartProcessMessage(StartProcessMessage message) {
		final String zipFilePath = message.getZipFilePath();
		if(! new File(zipFilePath).exists()) {
			throw new IllegalArgumentException("The path does not denote a valid existing zip file.");
		}
	}

	private void createAndDeployWorkers(StartProcessMessage message, Long processId, URL sharedFileURL) {
		List<RemoteAddress> availableRemoteAddresses = RemoteAddressProvider.getAvailableRemoteAddresses();
		
		List<ActorRef> processWorkers = new ArrayList<ActorRef>();
		for(RemoteAddress remoteAddress : availableRemoteAddresses) {
			final Address address = new Address(
					remoteAddress.getProtocol(),
					remoteAddress.getActorSystemName(), 
					remoteAddress.getIp(),
					remoteAddress.getPort());
			final int workersPerProcess = remoteAddress.getWorkersPerProcess();
			for(int i=0; i<workersPerProcess; i++) {
				final ActorRef passwordChecker;
				if(RUN_WITH_REMOTE_WORKERS) {
					passwordChecker = getContext().system().actorOf(Props.create(ZipPasswordBreakWorker.class).withDeploy(new Deploy(new RemoteScope(address))));
				} else {
					passwordChecker = getContext().actorOf(Props.create(ZipPasswordBreakWorker.class));
				}
				processWorkers.add(passwordChecker);
			}
			
			runningProcesses.put(processId, new Process(processWorkers, sharedFileURL, message.getZipFilePath()));
		}
	}

	private void startWorkers(Long processId) {
		Process process = runningProcesses.get(processId);
		List<ActorRef> processWorkers = process.workers;
		final int workersNum = processWorkers.size();
		for(int i=0; i<workersNum; i++) {
			processWorkers.get(i).tell(new StartWorkerMessage(processId, process.sharedFileCopyUrl, i, passwordChunkSize, workersNum), getSelf());
		}
	}
	
	private Long generateNewProcessId() {
		if(log.isInfoEnabled()) {
			log.info(getSelf().toString() + " -> entered generateNewProcessId()");
		}
		
		Long generated = random.nextLong();
		while(runningProcesses.containsKey(generated)) {
			generated = random.nextLong();
		}
		
		if(log.isInfoEnabled()) {
			log.info(getSelf().toString() + " -> exit generateNewProcessId() returning " + generated);
		}
		return generated;
	}

	private void endProcess(Long processId) {
		Process process = runningProcesses.get(processId);
		for(ActorRef worker : process.workers) {
			getContext().stop(worker); //TODO this doesn't seem to work properly
		}
		runningProcesses.remove(processId);
	}

	private void communicateResult(FoundPasswordMessage message) {
		Process process = runningProcesses.get(message.getProcessId());
		log.info("****************** Archive " + process.originalFilePath + " has been broken:\nSuccessfull password is: " + message.getSuccessfullPassword() + "\n\n****************************************");
		//TODO
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		if(log.isInfoEnabled()) {
			log.info(getSelf().toString() + " -> entered supervisorStrategy()");
		}

		if (supervisionStrategy == null) {

			supervisionStrategy =
			// After 5 exceptions within 10 seconds, the worker actor will be stopped.
			new OneForOneStrategy(5, Duration.create(10, TimeUnit.SECONDS),
					new Function<Throwable, Directive>() {

						public Directive apply(Throwable throwable) throws Exception {

							if(throwable instanceof PasswordsExhaustedException) {
								return OneForOneStrategy.stop();
							} else {
								return OneForOneStrategy.escalate();
							}
						}
					});
		}

		return supervisionStrategy;
	}

	private static class Process {
		List<ActorRef> workers;
		URL sharedFileCopyUrl;
		String originalFilePath;
		public Process(List<ActorRef> workers, URL sharedFileCopyUrl, String originalFilePath) {
			super();
			this.workers = workers;
			this.sharedFileCopyUrl = sharedFileCopyUrl;
			this.originalFilePath = originalFilePath;
		}
	}
}

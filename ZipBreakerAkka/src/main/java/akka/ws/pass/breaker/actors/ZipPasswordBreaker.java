package akka.ws.pass.breaker.actors;

import akka.event.Logging;
import akka.event.LoggingAdapter;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.japi.Function;
import akka.ws.pass.breaker.messages.BreakArchiveMessage;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.FoundPasswordMessage;
import akka.ws.pass.breaker.messages.NewProcessMessage;
import akka.ws.pass.breaker.messages.PasswordChunkMessage;
import akka.ws.pass.breaker.messages.RequestPasswordFlowMessage;
import akka.ws.pass.breaker.messages.StartFeedingProcessMessage;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
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

	final static String THIS_HOST = "192.168.166.100"; //TODO externalize in properties
	final static String PATH_TO_SHARED_FOLDER = "D:/share"; //TODO externalize in properties
	final static String SHARED_PATH_TO_SHARED_FOLDER = "file://EN61081/share"; //TODO externalize in properties

	private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

	ActorRef workDispatcher;
	ActorRef passwordProvider;

	private SupervisorStrategy strategy;
	
	private Set<Long> runningProcessesIds = new HashSet<>();
	
	private Random random = new Random();

	public void onReceive(Object message) throws Exception {

		if (message instanceof BreakArchiveMessage) {
			System.out.println("Master received start message");

			BreakArchiveMessage inMessage = (BreakArchiveMessage) message;
			final String zipFilePath = inMessage.getZipFilePath();
			if(! new File(zipFilePath).exists()) {
				throw new IllegalArgumentException("The path does not denote a valid existing zip file.");
			}
			
			Long processId = generateNewProcessId();
			URL fileURL = makeFileAvailable(new File(inMessage.getZipFilePath()));

			workDispatcher.tell(new NewProcessMessage(processId, fileURL), getSelf());
			

		} else if(message instanceof StartFeedingProcessMessage) {
			
			StartFeedingProcessMessage inMessage = (StartFeedingProcessMessage) message;
			passwordProvider.tell(new RequestPasswordFlowMessage(inMessage.getProcessId()), getSelf());
		} else if(message instanceof PasswordChunkMessage) {
			
			PasswordChunkMessage inMessage = (PasswordChunkMessage) message;
			FeedProcessMessage outMessage = new FeedProcessMessage(inMessage.getPasswordChunk(), inMessage.getProcessId());
			workDispatcher.tell(outMessage, getSelf());
		} else if(message instanceof FoundPasswordMessage) {
			
			FoundPasswordMessage inMessage = (FoundPasswordMessage) message;
			EndProcessMessage outMessage = new EndProcessMessage(inMessage.getProcessId());
			workDispatcher.tell(outMessage, getSelf());
			passwordProvider.tell(outMessage, getSelf());
		}
	}
	
	@Override
	public void preStart() {
		logger.info(getSelf().toString() + " -> entered preStart()");
		initialiseChildren();
		logger.info(getSelf().toString() + " exit preStart()");
	}

	private void initialiseChildren() {
		if(workDispatcher == null) {
			workDispatcher = getContext().actorOf(Props.create(WorkDispatcher.class), "workDispatcher");
		}
		if(passwordProvider == null) {
			passwordProvider = getContext().actorOf(Props.create(PasswordProvider.class), "passwordProvider");
		}
	}
	
	private URL makeFileAvailable(File source) throws IOException {
		final String fileName = source.getName();
		File destination = new File(PATH_TO_SHARED_FOLDER + "/" + fileName);
		Files.move(source, destination);
		URL url = new URL(SHARED_PATH_TO_SHARED_FOLDER + "/" + fileName);
		return url;
	}
	
	private Long generateNewProcessId() {
		Long generated = random.nextLong();
		while(runningProcessesIds.contains(generated)) {
			generated = random.nextLong();
		}
		runningProcessesIds.add(generated);
		return generated;
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {

		if (strategy == null) {

			strategy =
			// After 5 exceptions within 10 seconds, the worker actor will be stopped.
			new OneForOneStrategy(5, Duration.create(10, TimeUnit.SECONDS),
					new Function<Throwable, Directive>() {

						public Directive apply(Throwable throwable) throws Exception {

							// TODO analyze what kind of exceptions may occur here and what should be done for each
							return OneForOneStrategy.escalate();
						}
					});
		}

		return strategy;
	}
}

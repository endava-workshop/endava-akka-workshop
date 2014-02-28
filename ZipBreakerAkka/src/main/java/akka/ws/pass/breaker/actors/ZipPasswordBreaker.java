package akka.ws.pass.breaker.actors;

import akka.ws.pass.breaker.messages.BreakZipMessage;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.FoundPasswordMessage;
import akka.ws.pass.breaker.messages.NewProcessMessage;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.japi.Function;
import net.lingala.zip4j.core.ZipFile;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 
 * The ZipPasswordBreaker class 
 * 
 * @author Daniel DOBOGA
 *
 */
public class ZipPasswordBreaker extends UntypedActor {

	final static int PASSWORD_CHUNK_SIZE = 30;

	private ActorRef routingActor;

	private SupervisorStrategy strategy;

	public void onReceive(Object message) throws Exception {

		if (message instanceof BreakZipMessage) {
			System.out.println("Master received start message");
			initialiseChildren((BreakZipMessage) message);

			BreakZipMessage inMessage = (BreakZipMessage) message;
			final String zipFilePath = inMessage.getZipFilePath();
			if(! new File(zipFilePath).exists()) {
				throw new IllegalArgumentException("The path does not denote a valid existing zip file.");
			}
			
			ZipFile zipFile = new ZipFile(inMessage.getZipFilePath());
			long idProcess = zipFile.hashCode();
			// TODO Research the possibilities offered by AKKA I/O for asynchronous file transfer between actors
			// final ActorRef tcp = Tcp.get(getContext().system()).manager();
			routingActor.tell(new NewProcessMessage(idProcess, zipFile), getSelf());

			boolean passwordNotFound = true;
			do {
				List<String> passwords = generateNewChunkOfPasswords(PASSWORD_CHUNK_SIZE);
				FeedProcessMessage outMessage = new FeedProcessMessage(passwords, idProcess);
				routingActor.tell(outMessage, getSelf());
			}
			while (passwordNotFound);

		} else if(message instanceof FoundPasswordMessage) {
			FoundPasswordMessage inMessage = (FoundPasswordMessage) message;
			routingActor.tell(new EndProcessMessage(inMessage.getProcessId()), getSelf());
		}
	}

	private void initialiseChildren(BreakZipMessage message) {
		if(routingActor == null) {
			routingActor = this.getContext().actorOf(Props.create(RoutingActor.class), "routingActor");
		}
	}

	/**
	 * FIXME This method should feed from an external password database somehow. At this moment, it simply generates
	 * random printable character sequences with a maximum length of 50.
	 * 
	 * @param chunkSize
	 * @return List<String> containing a number of random Strings equal to chunkSize
	 */
	private static List<String> generateNewChunkOfPasswords(int chunkSize) {
		// FIXME 
		Random random = new Random();
		byte[] b = new byte[random.nextInt(50)];
		List<String> list = new ArrayList<String>(chunkSize);
		final String acceptedPasswordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_- ";
		final int maxPasswordLength = 50;

		for (int i = 0; i < chunkSize; i++) {
			int passwordLength = random.nextInt(maxPasswordLength);
			StringBuilder password = new StringBuilder(passwordLength);
			for (int j = 0; j < passwordLength; j++) {
				int charIndex = random.nextInt(acceptedPasswordChars.length());
				password.append(acceptedPasswordChars.charAt(charIndex));
			}
			list.add(password.toString());
		}
		list.add("pass");
		return list;
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

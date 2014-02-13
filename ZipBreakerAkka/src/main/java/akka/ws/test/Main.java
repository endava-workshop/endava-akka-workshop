package akka.ws.test;

import akka.ws.test.messages.BreakZipMessage;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.ws.test.actors.ZipPasswordBreaker;

/**
 * Zip password breaker with Akka.
 * Note: this project is currently a stub.
 * 
 * @author Daniel Doboga
 */
public class Main {

	public static void main(String[] args) {
		ActorSystem actorSystem = ActorSystem.create("MySystem");

		final int NUMBER_OF_WORKERS = 3;
		final String zipFilePath = "target/classes/experiment.zip";
		BreakZipMessage breakZipMessage = new BreakZipMessage(zipFilePath, NUMBER_OF_WORKERS);

		ActorRef processorActor = actorSystem.actorOf(Props.create(ZipPasswordBreaker.class));
		processorActor.tell(breakZipMessage, null);
	}
}

package akka.ws.pass.breaker;

import akka.ws.pass.breaker.messages.BreakZipMessage;

import akka.ws.pass.breaker.actors.ZipPasswordBreaker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Zip password breaker with Akka.
 * Note: this project is currently a stub.
 * 
 * @author Daniel Doboga
 */
public class LocalApplication {

	public static void main(String[] args) throws Exception {
		ActorSystem actorSystem = ActorSystem.create("LocalZipBreakerActorSystem");

		final int NUMBER_OF_WORKERS = 3;
		final String zipFilePath = "target/classes/experiment.zip";
		BreakZipMessage breakZipMessage = new BreakZipMessage(zipFilePath, NUMBER_OF_WORKERS);

		ActorRef processorActor = actorSystem.actorOf(Props.create(ZipPasswordBreaker.class));
		processorActor.tell(breakZipMessage, null);
		
	}
}

package akka.ws.pass.breaker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.ws.pass.breaker.actors.ZipPasswordBreaker;
import akka.ws.pass.breaker.messages.BreakArchiveMessage;

/**
 * Zip password breaker with Akka.
 * Example of use.
 * 
 * @author Daniel Doboga
 */
public class LocalApplication {
	
	final static String zipArchivePath = "D:/workspace/EndavaAkkaWs/ZipBreakerAkka/src/main/resources/experiment2.zip"; //beware this will be consumed after the first ran
	final static String LOCAL_ACTOR_SYSTEM_NAME = "LocalZipBreakerActorSystem"; //TODO externalize name in the properties

	public static void main(String[] args) throws Exception {
		ActorSystem actorSystem = ActorSystem.create(LOCAL_ACTOR_SYSTEM_NAME);
		
		BreakArchiveMessage breakZipMessage = new BreakArchiveMessage(zipArchivePath);

		ActorRef processorActor = actorSystem.actorOf(Props.create(ZipPasswordBreaker.class));
		processorActor.tell(breakZipMessage, null);
		
	}
}

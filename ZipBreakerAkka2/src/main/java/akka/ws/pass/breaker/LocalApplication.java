package akka.ws.pass.breaker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.ws.pass.breaker.actors.ZipPasswordBreaker;
import akka.ws.pass.breaker.messages.StartProcessMessage;
import akka.ws.pass.breaker.util.PropertyUtil;

/**
 * Zip password breaker with Akka.
 * Example of use.
 * 
 * @author Daniel Doboga
 */
public class LocalApplication {
	
	final static String zipArchivePath = "D:/workspace/EndavaAkkaWs/ZipBreakerAkka2/src/main/resources/experiment_16000.zip";
	final static String LOCAL_ACTOR_SYSTEM_NAME = PropertyUtil.getStringProperty("local.actor.system.name");
	public static long startTime;

	public static void main(String[] args) throws Exception {
		startTime = System.currentTimeMillis();
		ActorSystem actorSystem = ActorSystem.create(LOCAL_ACTOR_SYSTEM_NAME);
		
		StartProcessMessage breakZipMessage = new StartProcessMessage(zipArchivePath);

		ActorRef processorActor = actorSystem.actorOf(Props.create(ZipPasswordBreaker.class));
		
		processorActor.tell(breakZipMessage, null);
		
	}
}

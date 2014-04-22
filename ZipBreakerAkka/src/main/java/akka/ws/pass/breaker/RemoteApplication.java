package akka.ws.pass.breaker;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.kernel.Bootable;
import akka.ws.pass.breaker.actors.ZipPasswordBreakWorker;

/**
 * The RemoteApplication class represents the entry point for the remote nodes 
 * that will be deployed in Akka microkernels.
 * 
 * @author ddoboga
 */
public class RemoteApplication implements Bootable {
	
	public static final String ACTORY_SYSTEM_NAME = "ZipBreakerRemoteSystem";
	private final ActorSystem actorSystem = ActorSystem.create(ACTORY_SYSTEM_NAME);

	public void shutdown() {
		actorSystem.shutdown();
	}

	public void startup() {
//		//start the workers and let them ready to process messages
//		actorSystem.actorOf(Props.create(ZipPasswordBreakWorker.class), "workerRouter");
	}

}

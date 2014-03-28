package akka.ws.pass.breaker;

import akka.ws.pass.breaker.util.PropertyUtil;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.kernel.Bootable;
import akka.ws.pass.breaker.actors.ZipPasswordBreakWorker;

public class RemoteApplication implements Bootable {
	
	private static final String ACTORY_SYSTEM_NAME_KEY = "actor.system.name";
	
	private ActorSystem actorSystem;

	public void shutdown() {
		actorSystem.shutdown();
	}

	public void startup() {
		actorSystem = ActorSystem.create(PropertyUtil.getRemoteProperty(ACTORY_SYSTEM_NAME_KEY));
		//start the workers and let them ready to process messages
		actorSystem.actorOf(Props.create(ZipPasswordBreakWorker.class), "workerRouter");
	}

}

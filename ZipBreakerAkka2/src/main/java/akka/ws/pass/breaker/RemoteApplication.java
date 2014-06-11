package akka.ws.pass.breaker;

import akka.ws.pass.breaker.util.PropertyUtil;

import akka.actor.ActorSystem;
import akka.kernel.Bootable;

/**
 * The RemoteApplication class represents the entry point for the remote nodes 
 * that will be deployed in Akka microkernels.
 * 
 * @author ddoboga
 */
public class RemoteApplication implements Bootable {
	
	public static final String ACTORY_SYSTEM_NAME = PropertyUtil.getStringProperty("remote.actor.system.name");
	private final ActorSystem actorSystem = ActorSystem.create(ACTORY_SYSTEM_NAME);

	public void shutdown() {
		actorSystem.shutdown();
	}

	public void startup() {
		//do nothing; all remote actors will be deployed at runtime.
	}

}

package akka.ws.pass.breaker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.kernel.Bootable;
import akka.routing.RoundRobinRouter;
import akka.ws.pass.breaker.actors.ZipPasswordBreakWorker;

import java.io.IOException;
import java.util.Properties;

public class RemoteApplication implements Bootable {
	
	private static final String PROPERTIES_FILE_NAME = "remote.properties";
	private static final String WORKER_NUM_KEY = "worker.actor.number";
	
	private ActorSystem actorSystem;

	public void shutdown() {
		actorSystem.shutdown();
	}

	public void startup() {
		actorSystem = ActorSystem.create("ZipBreakerRemoteActorSystem");

		//start the workers and let them ready to process messages
		actorSystem.actorOf(Props.create(ZipPasswordBreakWorker.class)
				.withRouter(new RoundRobinRouter(getWorkerActorNumber())), "workerRouter");
	}
	
	private static int getWorkerActorNumber() {
		Properties props = new Properties();
		try {
			props.load(RemoteApplication.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME));
		}
		catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		String value = (String) props.get(WORKER_NUM_KEY);
		return Integer.parseInt(value);
	}

}

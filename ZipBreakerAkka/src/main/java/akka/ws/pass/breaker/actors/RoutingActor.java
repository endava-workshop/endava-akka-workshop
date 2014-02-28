package akka.ws.pass.breaker.actors;

import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.NewProcessMessage;

import akka.ws.pass.breaker.settings.RemoteAddress;
import akka.ws.pass.breaker.settings.RemoteAddressProvider;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import scala.concurrent.duration.Duration;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The RoutingActor class represents the actor that acts like a router for the worker remote machines. This actor
 * manages the worker actors distribution on remote machines and the routing policy.
 * 
 * @author Daniel DOBOGA
 */
public class RoutingActor extends UntypedActor {
	final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private List<ActorSelection> workerRouters;
	private ActorRef masterActor;
	private SupervisorStrategy strategy;
	private int lastWorkerIndex;

	public void onReceive(final Object message) throws Exception {

		if (message instanceof NewProcessMessage) {
			initialiseThirdParties();
			routeToAllWorkers((NewProcessMessage) message);
		} else if(message instanceof FeedProcessMessage) {
			routeToOneWorker((FeedProcessMessage) message);
		}
	}

	private void initialiseThirdParties() {
		if(masterActor == null) {
			masterActor = getSender();
		}
		if(workerRouters == null) {
			initialiseWorkers();
		}
	}
	
	private void initialiseWorkers() {
		workerRouters = new ArrayList<ActorSelection>();
		final List<RemoteAddress> remoteAddresses = RemoteAddressProvider.getAvailableRemoteAddresses();
		for(RemoteAddress remoteAddress : remoteAddresses) {
			try {
				StringBuilder actorPath = new StringBuilder("akka.");
				actorPath.append(remoteAddress.getProtocol());
				actorPath.append("://");
				actorPath.append(remoteAddress.getActorSystemName());
				actorPath.append("@");
				actorPath.append(remoteAddress.getIp());
				actorPath.append(":");
				actorPath.append(remoteAddress.getPort());
				actorPath.append("/user/workerRouter");
				ActorSelection remoteWorker = getContext().actorSelection(actorPath.toString());
				workerRouters.add(remoteWorker);
			}
			catch (Exception e) {
				StringWriter errWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(errWriter));
				log.error(errWriter.toString());
			}
		}
	}

	private void routeToAllWorkers(Serializable message) {
		for(ActorSelection remoteWorker : workerRouters) {
			remoteWorker.tell(message, getSender());
		}
	}
	
	/**
	 * Implement a round robin policy for remote machines. Behind each of these machines there should be its own router
	 * feeding several local actors.
	 * 
	 * @param message
	 */
	private void routeToOneWorker(Serializable message) {
		if(lastWorkerIndex == workerRouters.size() - 1) {
			lastWorkerIndex = 0;
		} else {
			lastWorkerIndex ++;
		}
		
		workerRouters.get(lastWorkerIndex).tell(message, getSender());
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

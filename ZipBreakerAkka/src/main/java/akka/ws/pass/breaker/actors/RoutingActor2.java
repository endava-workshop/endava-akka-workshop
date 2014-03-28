package akka.ws.pass.breaker.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.remote.routing.RemoteRouterConfig;
import akka.routing.BroadcastRouter;
import akka.routing.SmallestMailboxRouter;
import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.NewProcessMessage;
import akka.ws.pass.breaker.settings.RemoteAddress;
import akka.ws.pass.breaker.settings.RemoteAddressProvider;
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
public class RoutingActor2 extends UntypedActor {
	final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private Address[] workerAddresses;
	private ActorRef balancingWorkersRouter;
	private ActorRef broadcastWorkersRouter;
	private ActorRef masterActor;
	private SupervisorStrategy strategy;

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
		if(workerAddresses == null) {
			initialiseWorkers();
		}
	}
	
	private void initialiseWorkers() {
		List<Address> addressesList = new ArrayList<Address>();
		final List<RemoteAddress> remoteAddresses = RemoteAddressProvider.getAvailableRemoteAddresses();
		for(RemoteAddress remoteAddress : remoteAddresses) {
			try {
				Address address = new Address(
						remoteAddress.getProtocol(), 
						remoteAddress.getActorSystemName(), 
						remoteAddress.getIp(), 
						remoteAddress.getPort()
						);
				addressesList.add(address);
			}
			catch (Exception e) {
				StringWriter errWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(errWriter));
				log.error(errWriter.toString());
			}
		}
		workerAddresses = addressesList.toArray(new Address[0]);

		final int noOfRemoteMachines = workerAddresses.length;
		balancingWorkersRouter = getContext().system().actorOf(Props.create(ZipPasswordBreakWorker.class).withRouter(new RemoteRouterConfig(new SmallestMailboxRouter(noOfRemoteMachines), addressesList)));
		broadcastWorkersRouter = getContext().system().actorOf(Props.create(ZipPasswordBreakWorker.class).withRouter(new RemoteRouterConfig(new BroadcastRouter(noOfRemoteMachines), addressesList)));
	}

	private void routeToAllWorkers(Serializable message) {
		broadcastWorkersRouter.tell(message, getSender());
	}
	
	/**
	 * Implement a load balancing policy for remote machines. Behind each of these machines there should be its own router
	 * feeding several local actors.
	 * 
	 * @param message
	 */
	private void routeToOneWorker(Serializable message) {
		balancingWorkersRouter.tell(message, getSender());
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

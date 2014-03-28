package akka.ws.pass.breaker.actors;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.japi.Function;
import akka.remote.routing.RemoteRouterConfig;
import akka.routing.BroadcastRouter;
import akka.routing.SmallestMailboxRouter;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.NewProcessMessage;
import akka.ws.pass.breaker.messages.ReadyToProcessMessage;
import akka.ws.pass.breaker.messages.StartFeedingProcessMessage;
import akka.ws.pass.breaker.settings.RemoteAddress;
import akka.ws.pass.breaker.settings.RemoteAddressProvider;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The WorkDispatcher class is intended to dispatch the work to the remote machines.
 * 
 * @author Daniel DOBOGA
 */
public class WorkDispatcher extends UntypedActor {

	private SupervisorStrategy strategy;
	private ActorRef zipPasswordBreaker;
	private ActorRef remoteBroadcastRouter;
	private List<Address> allAvailableRemoteAddresses;
	private Map<Long, ActorRef> remoteBalancingRouters = new HashMap<>();
	private Map<Long, List<Address>> remoteBalancingRoutersAddresses = new HashMap<>();
	
	/* TODO: the remoteBalancingRouters could be retrieved directly from the ZipPasswordBreakWorker, via the ReadoToProcessMessage*/
	
	public void onReceive(Object message) throws Exception {

		if (message instanceof NewProcessMessage) {

			NewProcessMessage inMessage = (NewProcessMessage) message;
			zipPasswordBreaker = getSender();
			if(notInitialised(inMessage.getIdProcess())) {
				initChildren();
			}
			remoteBroadcastRouter.tell(inMessage, getSelf());

		} else if(message instanceof FeedProcessMessage) {
			
			FeedProcessMessage inMessage = (FeedProcessMessage) message;
			remoteBalancingRouters.get(inMessage.getProcessId()).tell(message, getSender());
		} else if(message instanceof EndProcessMessage) {
			
			remoteBroadcastRouter.tell(message, getSender());
			interruptProcess((EndProcessMessage) message);
		} else if(message instanceof ReadyToProcessMessage) {
			
			ReadyToProcessMessage inMessage = (ReadyToProcessMessage) message;
			if(isFirstReadyChildForThisProcess(inMessage.getProcessId())) {
				zipPasswordBreaker.tell(new StartFeedingProcessMessage(inMessage.getProcessId()), getSelf());
			}

			includeChildToBalancingRouter(inMessage.getProcessId(), getSender());
		}
	}
	
	private void initChildren() {
		List<RemoteAddress> availableRemoteAddresses = RemoteAddressProvider.getAvailableRemoteAddresses();
		List<Address> remoteAddresses = new ArrayList<>(availableRemoteAddresses.size());
		
		for(RemoteAddress availableAddress : availableRemoteAddresses) {
			Address address = new Address(
					availableAddress.getProtocol(),
					availableAddress.getActorSystemName(), 
					availableAddress.getIp(),
					availableAddress.getPort());
			remoteAddresses.add(address);
		}
		allAvailableRemoteAddresses = remoteAddresses;
		
		remoteBroadcastRouter = getContext()
				.actorOf(
						Props.create(ZipPasswordBreakWorker.class)
						.withRouter(
								new RemoteRouterConfig(
										new BroadcastRouter(remoteAddresses.size()), remoteAddresses
										)
								)
						);
		
	}
	
	private boolean notInitialised(Long processId) {
		return remoteBroadcastRouter == null;
	}
	
	private boolean isFirstReadyChildForThisProcess(Long processId) {
		return ! remoteBalancingRouters.containsKey(processId);
	}
	
	private void includeChildToBalancingRouter(Long processId, ActorRef remoteChild) {
		Address balancingRouterAddressOnRemoteMachine = getRelatedAvailableRemoteAddress(remoteChild);
		List<Address> addressesBoundToProcess = remoteBalancingRoutersAddresses.get(processId);
		if(addressesBoundToProcess == null) {
			addressesBoundToProcess = new ArrayList<>(allAvailableRemoteAddresses.size());
			remoteBalancingRoutersAddresses.put(processId, addressesBoundToProcess);
		}
		addressesBoundToProcess.add(balancingRouterAddressOnRemoteMachine);
		ActorRef remoteBalancingRouter = getContext()
				.actorOf(
						Props.create(ZipPasswordBreakWorker.class)
						.withRouter(
								new RemoteRouterConfig(
										new SmallestMailboxRouter(addressesBoundToProcess.size()), addressesBoundToProcess
										)
								)
						);
		remoteBalancingRouters.put(processId, remoteBalancingRouter);
	}
	
	private void interruptProcess(EndProcessMessage msg) {
		this.remoteBalancingRoutersAddresses.remove(msg.getProcessId());
		this.remoteBalancingRouters.remove(msg.getProcessId());
	}
	
	private Address getRelatedAvailableRemoteAddress(ActorRef ref) {
		ActorPath childPath = ref.path();
		Address childAddress = childPath.address();
		for(Address address : allAvailableRemoteAddresses) {
			if(address.host().equals(childAddress.host()) 
					&& address.port().equals(childAddress.host()) 
					&& address.system().equals(childAddress.system())
					) {
				return address;
			}
		}
		return null;
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

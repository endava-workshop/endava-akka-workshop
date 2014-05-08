package akka.ws.pass.breaker.actors;

import static akka.ws.pass.breaker.util.Utils.getFullStackTrace;
import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.Deploy;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.remote.RemoteScope;
import akka.routing.BroadcastRouter;
import akka.routing.RoundRobinRouter;
import akka.routing.SmallestMailboxRouter;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.NewProcessMessage;
import akka.ws.pass.breaker.messages.ReadyToProcessMessage;
import akka.ws.pass.breaker.messages.RegisterPasswordCheckersMessage;
import akka.ws.pass.breaker.messages.StartFeedingProcessMessage;
import akka.ws.pass.breaker.settings.RemoteAddress;
import akka.ws.pass.breaker.settings.RemoteAddressProvider;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The WorkDispatcher class is intended to dispatch the work to the remote machines.
 * 
 * @author Daniel DOBOGA
 */
public class WorkDispatcher extends UntypedActor {
	
	public static final int PASSWORD_CHECKERS_PER_REMOTE_MACHINE = 4;

	private SupervisorStrategy supervisionStrategy;
	private ActorRef zipPasswordBreaker;
	private ActorRef remoteBroadcastRouter;
	private Map<Long, ActorRef> remoteBalancingRouters = new HashMap<>();
	
	private List<RemoteMachine> availableRemoteMachines = new ArrayList<>();

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	public void onReceive(Object message) throws Exception {
		if(log.isInfoEnabled()) {
			log.info("\n\n*********************************\nWorkDispatcher received " + message + "\n****************************\n\n");
		}

		if (message instanceof NewProcessMessage) {

			NewProcessMessage inMessage = (NewProcessMessage) message;
			zipPasswordBreaker = getSender();
			if(notInitialised(inMessage.getIdProcess())) {
				initChildren();
			}
			remoteBroadcastRouter.tell(inMessage, getSelf());

		} else if(message instanceof FeedProcessMessage) {
			
			FeedProcessMessage inMessage = (FeedProcessMessage) message;
			ActorRef targetRemoteWorker = remoteBalancingRouters.get(inMessage.getProcessId());
			if(targetRemoteWorker == null) {
				// do not propel it further! most probably the process has been interrupted
				return;
			} else {
				targetRemoteWorker.tell(message, getSender());
			}
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
		if(log.isInfoEnabled()) {
			log.info("Entered initChildren()");
		}
		List<RemoteAddress> availableRemoteAddresses = RemoteAddressProvider.getAvailableRemoteAddresses();
		List<Address> remoteAddresses = new ArrayList<>(availableRemoteAddresses.size());
		
		for(RemoteAddress availableAddress : availableRemoteAddresses) {
			final Address address = new Address(
					availableAddress.getProtocol(),
					availableAddress.getActorSystemName(), 
					availableAddress.getIp(),
					availableAddress.getPort());
			remoteAddresses.add(address);
			
			RemoteMachine remoteMachine = new RemoteMachine(address);
			availableRemoteMachines.add(remoteMachine);

			remoteMachine.zipPasswordBreakWorker = getContext().actorOf(Props.create(ZipPasswordBreakWorker.class).withDeploy(new Deploy(new RemoteScope(remoteMachine.address))));
			remoteMachine.zipFileDownloader = getContext().actorOf(Props.create(ZipFileDownloader.class).withDeploy(new Deploy(new RemoteScope(remoteMachine.address))));
			for(int i=0; i<PASSWORD_CHECKERS_PER_REMOTE_MACHINE; i++) {
				remoteMachine.passwordCheckers.add(getContext().actorOf(Props.create(PasswordChecker.class).withDeploy(new Deploy(new RemoteScope(remoteMachine.address)))));
			}
			remoteMachine.passwordCheckersBroadcastRouter = getContext().actorOf(Props.empty().withRouter(BroadcastRouter.create(remoteMachine.passwordCheckers)));
			remoteMachine.passwordCheckersBalancingRouter = getContext().actorOf(Props.empty().withRouter(RoundRobinRouter.create(remoteMachine.passwordCheckers)));
			remoteMachine.zipPasswordBreakWorker.tell(new RegisterPasswordCheckersMessage(remoteMachine.passwordCheckersBroadcastRouter), getSelf());
		}
		
		remoteBroadcastRouter = getContext().actorOf(Props.empty().withRouter(BroadcastRouter.create(allRemotePasswordBreakWorkers())));

		if(log.isInfoEnabled()) {
			log.info("exit initChildren()");
		}
	}
	
	private boolean notInitialised(Long processId) {
		return remoteBroadcastRouter == null;
	}
	
	private boolean isFirstReadyChildForThisProcess(Long processId) {
		return ! remoteBalancingRouters.containsKey(processId);
	}
	
	private void includeChildToBalancingRouter(Long processId, ActorRef remoteChild) {
		Address remoteMachineAddress = getRelatedAvailableRemoteAddress(remoteChild);
		
		RemoteMachine remoteMachine = new RemoteMachine(remoteMachineAddress);
		if(availableRemoteMachines.contains(remoteMachine)) {
			int index = Collections.binarySearch(availableRemoteMachines, remoteMachine);
			remoteMachine = availableRemoteMachines.get(index);
		} else {
			return;
		}
		remoteMachine.processesReadyToFeed.add(processId);
		ActorRef remoteBalancingRouter = getContext().actorOf(Props.empty().withRouter(RoundRobinRouter.create(allRemotePasswordCheckers())));
		remoteBalancingRouters.put(processId, remoteBalancingRouter);
	}
	
	private void interruptProcess(EndProcessMessage msg) {
		this.remoteBalancingRouters.remove(msg.getProcessId());
	}
	
	private List<ActorRef> allRemotePasswordBreakWorkers() {
		List<ActorRef> list = new ArrayList<>(availableRemoteMachines.size());
		for(RemoteMachine remoteMachine : availableRemoteMachines) {
			list.add(remoteMachine.zipPasswordBreakWorker);
		}
		return list;
	}
	
	private List<Address> allRemoteMachinesAddresses() {
		List<Address> list = new ArrayList<>(availableRemoteMachines.size());
		for(RemoteMachine remoteMachine : availableRemoteMachines) {
			list.add(remoteMachine.address);
		}
		return list;
	}
	
	private List<ActorRef> allRemotePasswordCheckersBalancingRouters() {
		List<ActorRef> list = new ArrayList<>(availableRemoteMachines.size());
		for(RemoteMachine remoteMachine : availableRemoteMachines) {
			list.add(remoteMachine.passwordCheckersBalancingRouter);
		}
		return list;
	}
	
	private List<ActorRef> allRemotePasswordCheckers() {
		List<ActorRef> list = new ArrayList<>(availableRemoteMachines.size() * PASSWORD_CHECKERS_PER_REMOTE_MACHINE);
		for(RemoteMachine remoteMachine : availableRemoteMachines) {
			list.addAll(remoteMachine.passwordCheckers);
		}
		return list;
	}
	
	private Address getRelatedAvailableRemoteAddress(ActorRef ref) {
		ActorPath childPath = ref.path();
		Address childAddress = childPath.address();
		for(Address address : allRemoteMachinesAddresses()) {
			if(address.host().equals(childAddress.host()) 
					&& address.port().equals(childAddress.port()) 
					&& address.system().equals(childAddress.system())
					) {
				return address;
			}
		}
		return null;
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		if(log.isInfoEnabled()) {
			log.info(getSelf().toString() + " -> entered supervisorStrategy()");
		}

		if (supervisionStrategy == null) {

			supervisionStrategy =
			// After 5 exceptions within 10 seconds, the worker actor will be stopped.
			new OneForOneStrategy(5, Duration.create(10, TimeUnit.SECONDS),
					new Function<Throwable, Directive>() {

						public Directive apply(Throwable throwable) throws Exception {

							// TODO analyze what kind of exceptions may occur here and what should be done for each
							log.error(getFullStackTrace(throwable));
							return OneForOneStrategy.escalate();
						}
					});
		}

		return supervisionStrategy;
	}
	
	private static class RemoteMachine implements Comparable<RemoteMachine> {
		Address address;
		ActorRef zipPasswordBreakWorker;
		List<ActorRef> passwordCheckers = new ArrayList<>();
		Set<Long> processesReadyToFeed = new HashSet<>();
		ActorRef passwordCheckersBroadcastRouter;
		ActorRef passwordCheckersBalancingRouter;
		ActorRef zipFileDownloader;
		public RemoteMachine(Address address) {
			super();
			this.address = address;
			
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((address == null) ? 0 : address.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RemoteMachine other = (RemoteMachine) obj;
			if (address == null) {
				if (other.address != null)
					return false;
			}
			else if (!address.equals(other.address))
				return false;
			return true;
		}
		@Override
		public int compareTo(RemoteMachine other) {
			if(address == null) {
				return other.address == null ? 0 : -1;
			} else if(other.address == null) {
				return 1;
			} else {
				return address.toString().compareTo(other.address.toString());
			}
		}
	}

}

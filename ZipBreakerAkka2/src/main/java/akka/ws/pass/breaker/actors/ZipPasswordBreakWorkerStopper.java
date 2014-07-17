package akka.ws.pass.breaker.actors;

import akka.actor.UntypedActor;
import akka.ws.pass.breaker.messages.StopProcessMessage;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 
 * The ZipPasswordBreakWorkerStopper class 
 * 
 * @author Daniel DOBOGA
 *
 */
public class ZipPasswordBreakWorkerStopper extends UntypedActor {
	
	private static Set<Long> stoppedProcesses = new CopyOnWriteArraySet<>();

	public void onReceive(Object message) throws Exception {
		if(message instanceof StopProcessMessage) {
			stoppedProcesses.add(((StopProcessMessage) message).getProcessId());
			getContext().stop(getSelf()); //we only need this actor for this single moment.
		} else {
			throw new IllegalArgumentException("Unsupported message type: " + message.getClass());
		}
	}

	public static final boolean isStopped(long processId) {
		return stoppedProcesses.contains(Long.valueOf(processId));
		//TODO As remaining process IDs in the map may be a memory leak, should schedule at this moment a timer to remove this process id after a delay enough for all other workers to process at least one more chunk.
	}
	
}

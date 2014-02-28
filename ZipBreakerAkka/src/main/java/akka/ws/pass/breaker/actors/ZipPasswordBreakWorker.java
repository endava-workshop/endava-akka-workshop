package akka.ws.pass.breaker.actors;

import akka.ws.pass.breaker.service.impl.ZipPasswordBreakServiceImpl;

import akka.ws.pass.breaker.service.ZipPasswordBreakService;

import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.FoundPasswordMessage;
import akka.ws.pass.breaker.messages.NewProcessMessage;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import net.lingala.zip4j.core.ZipFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * The ZipPasswordBreakWorker class 
 * 
 * @author Daniel DOBOGA
 *
 */
public class ZipPasswordBreakWorker extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private ZipPasswordBreakService zipPasswordBreakService = new ZipPasswordBreakServiceImpl();
	private Map<Long, ZipFile> runningProcesses = new HashMap<Long, ZipFile>();

	public void onReceive(Object message) throws Exception {
		if(log.isDebugEnabled()) {
			log.debug("Worker received " + message.getClass());
		}
		
		if(message instanceof NewProcessMessage) {
			NewProcessMessage inMessage = (NewProcessMessage) message;
			runningProcesses.put(inMessage.getIdProcess(), inMessage.getZipFile());
			
		} else if (message instanceof FeedProcessMessage) {
			final FeedProcessMessage inMessage = (FeedProcessMessage) message;
			final long processId = inMessage.getIdProcess();
			ZipFile zipFile = runningProcesses.get(processId);
			
			if(zipFile == null) {
				//this means the process has been stopped; so passwords for this process should not be processed anymore
				return;
			}

			for (String password : inMessage.getPasswords()) {
				processOnePassword(zipFile, password, processId);
			}

		} else {
			throw new IllegalArgumentException("Unsupported message type: " + message.getClass());
		}
	}
	
	private void processOnePassword(final ZipFile zipFile, final String password, final long processId) {
		if (zipPasswordBreakService.checkPassword(zipFile, password)) {
			log.info("PASSWORD BROKEN ******************!\nFound password:" + password);
			runningProcesses.remove(processId);
			FoundPasswordMessage outMessage = new FoundPasswordMessage(processId, password);
			getSender().tell(outMessage, getSelf());
		}
		else {
			if(log.isDebugEnabled()) {
				log.debug("Wrong password: " + password);
			}
		}
	}
}

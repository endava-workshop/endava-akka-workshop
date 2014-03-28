package akka.ws.pass.breaker.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.FoundPasswordMessage;
import akka.ws.pass.breaker.messages.StartNewProcessMessage;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * The ZipPasswordBreakWorker class 
 * 
 * @author Daniel DOBOGA
 *
 */
public class PasswordChecker extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private static final AtomicSequence sequence = new AtomicSequence();
	
	private Map<Long, ZipFile> runningProcesses = new HashMap<>();

	public void onReceive(Object message) throws Exception {
		if(log.isDebugEnabled()) {
			log.debug("PasswordChecker received " + message.getClass());
		}
		
		if(message instanceof StartNewProcessMessage) {
			StartNewProcessMessage inMessage = (StartNewProcessMessage) message;
			runningProcesses.put(inMessage.getProcessId(), new ZipFile(inMessage.getZipFile()));
			
		} else if(message instanceof FeedProcessMessage) {
			FeedProcessMessage inMessage = (FeedProcessMessage) message;
			final Long processId = inMessage.getProcessId();
			if(runningProcesses.containsKey(Long.valueOf(processId))) {
				ZipFile zipFile = runningProcesses.get(processId);
				for(String password : inMessage.getPasswords()) {
					processOnePassword(zipFile, password, processId);
				}
			}
			
		} else if (message instanceof EndProcessMessage) {
			EndProcessMessage inMessage = (EndProcessMessage) message;
			runningProcesses.remove(inMessage.getProcessId());
			
		} else {
			throw new IllegalArgumentException("Unsupported message type: " + message.getClass());
		}
	}
	
	private void processOnePassword(final ZipFile zipFile, final String password, final long processId) {
		if (checkPassword(zipFile, password)) {
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
	
	
	

	/**
	 * {@inheritDoc}
	 */
	public boolean checkPassword(ZipFile zipFile, String password) {
		String destination = zipFile.getFile().getParent() + File.separatorChar + sequence.next();
		try {
			zipFile.setPassword(password);
			zipFile.extractAll(destination);
			return true;
		}
		catch (ZipException e) {
			return false;
		}
		finally {
			File destinationFolder = new File(destination);
			if (destinationFolder.exists()) {
				delete(destinationFolder);
			}
		}
	}

	/**
	 * Deletes the given file. If the file is directory, it recursively deletes.
	 * @param theFile
	 */
	private static void delete(File theFile) {
		if (theFile.isDirectory()) {
			for (File child : theFile.listFiles()) {
				delete(child);
			}

		}
		theFile.delete();
	}

	/**
	 * The AtomicSequence class is intended to offer String values that are unique within a small horizon of time.
	 * 
	 * @author ddoboga
	 */
	private static class AtomicSequence {
		private static final int DELTA = 10;

		private static final int SEQUENCE_LIMIT = Integer.MAX_VALUE - DELTA;

		private static final String SEED = "tmpunzprz_";

		private int sequence = 0;

		/**
		 * @return a String that is guaranteed to be different from the ones generated recently. It is possible that
		 *         this method return a sequence that was returned long ago, but not within the past few minutes. This
		 *         is intended to offer unique identifiers to differentiate processes that occur nearly at the same time
		 *         and live a short period of time (typically only few nanoseconds).
		 */
		public synchronized String next() {
			if (sequence >= SEQUENCE_LIMIT) {
				sequence = 0;
			}
			return SEED + ++sequence;
		}
	}
}

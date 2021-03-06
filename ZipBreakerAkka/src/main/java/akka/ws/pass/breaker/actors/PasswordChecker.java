package akka.ws.pass.breaker.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.FoundPasswordMessage;
import akka.ws.pass.breaker.messages.RequestTotalSpentTimeMessage;
import akka.ws.pass.breaker.messages.StartNewProcessMessage;
import akka.ws.pass.breaker.messages.TotalSpentTimeMessage;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
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
	
	private ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	
	private BigInteger totalSpentCPUTime = BigInteger.ZERO;
	
	private Map<Long, ZipFile> runningProcesses = new HashMap<>();

	public void onReceive(Object message) throws Exception {
		final long startTime = threadMXBean.getCurrentThreadCpuTime();
		if(log.isInfoEnabled()) {
			log.info("\n\n******** PasswordChecker received " + message + "\n***************************\n\n");
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
				logProcessedChunkInformation(inMessage);
			}
			
		} else if (message instanceof EndProcessMessage) {
			EndProcessMessage inMessage = (EndProcessMessage) message;
			deleteLocalFileCopy(runningProcesses.get(inMessage.getProcessId()));
			runningProcesses.remove(inMessage.getProcessId());
			
		} else if (message instanceof RequestTotalSpentTimeMessage) {
			TotalSpentTimeMessage outMessage = new TotalSpentTimeMessage(totalSpentCPUTime);
			getSender().tell(outMessage, getSelf());
			
		} else {
			throw new IllegalArgumentException("Unsupported message type: " + message.getClass());
		}
		final long endTime = threadMXBean.getCurrentThreadCpuTime();
		totalSpentCPUTime = totalSpentCPUTime.add(BigInteger.valueOf(endTime - startTime));
	}
	
	private void processOnePassword(final ZipFile zipFile, final String password, final long processId) {
		if (checkPassword(zipFile, password)) {
			log.info("\nPASSWORD BROKEN ******************!\nFound password:" + password);
			FoundPasswordMessage outMessage = new FoundPasswordMessage(processId, password);
			getSender().tell(outMessage, getSelf());
		}
		else {
//			if(log.isInfoEnabled()) {
//				log.info("Wrong password: " + password);
//			}
//just avoid poluting the log. Uncomnent this only temporary, if really needed.
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
			File[] children = theFile.listFiles();
			if(children != null) {
				for (File child : children) {
					if(child != null && child.exists()) {
						delete(child);
					}
				}
			}
		}
		theFile.delete();
	}

	/*
	 * No matter what you synchronize this on; we put the "synchronized" only to ensure atomicity of the operations
	 * inside this method which run on same thread.
	 */
	private static synchronized void deleteLocalFileCopy(ZipFile zipFile) {
		File file = zipFile.getFile();
		if(file.exists()) {
			file.delete();
		}
	}

	private void logProcessedChunkInformation(FeedProcessMessage processedChunk) {
		if(log.isInfoEnabled()) {
			String first = null;
			String last = null;
			final Iterator<String> iterator = processedChunk.getPasswords().iterator();
		    first = iterator.next();
		    last = first;
		    while(iterator.hasNext()) {
		        last = iterator.next();
		    }

			StringBuilder message = new StringBuilder();
			message.append("\n****************************** Checked chunk of ")
			.append(processedChunk.getPasswords().size())
			.append(" passwords. ***************************\n")
			.append("first password in chunk: ").append(first)
			.append("last password in chunk: ").append(last)
			.append("\n***********************************************\n");
			
			log.info(message.toString());
		}
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

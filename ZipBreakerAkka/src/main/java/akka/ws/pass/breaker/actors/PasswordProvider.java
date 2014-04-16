package akka.ws.pass.breaker.actors;

import akka.event.Logging;
import akka.event.LoggingAdapter;

import akka.ws.pass.breaker.messages.RequestTotalSpentTimeMessage;
import akka.ws.pass.breaker.messages.TotalSpentTimeMessage;

import akka.actor.UntypedActor;
import akka.ws.pass.breaker.messages.ContinuePasswordFlowMessage;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.PasswordChunkMessage;
import akka.ws.pass.breaker.messages.RequestPasswordFlowMessage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The PasswordProvider class is intended to sequentially provide password chunks aquired from different sources.
 * 
 * @author Daniel DOBOGA
 */
public class PasswordProvider extends UntypedActor {

	final static int PASSWORD_CHUNK_SIZE = 1000;
	final static String PASSWORD_FILE_NAME = "common_passwords.txt";

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private Map<Long, Cursor> cursors = new HashMap<>();
	
	private ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	
	private BigInteger totalSpentCPUTime = BigInteger.ZERO;

	public void onReceive(Object message) throws Exception {
		final long startTime = threadMXBean.getCurrentThreadCpuTime();
		if(log.isInfoEnabled()) {
			log.info("\n\n************* PasswordProvider received " + message + "\n*************************\n\n");
		}

		if (message instanceof RequestPasswordFlowMessage) {

			RequestPasswordFlowMessage inMessage = (RequestPasswordFlowMessage) message;
			final long processId = inMessage.getProcessId();
			initCursor(processId);
			
			ContinuePasswordFlowMessage outMessage = new ContinuePasswordFlowMessage(processId);
			getSelf().tell(outMessage, getSender());

		} else if(message instanceof ContinuePasswordFlowMessage) {
			
			ContinuePasswordFlowMessage inMessage = (ContinuePasswordFlowMessage) message;
			final long processId = inMessage.getProcessId();
			if(processNotEnded(processId) && morePasswordsAvailable(processId)) {
				Collection<String> passwordChunk = nextPasswordChunk(processId);
				PasswordChunkMessage outMessage = new PasswordChunkMessage(processId, passwordChunk);
				getSender().tell(outMessage, getSelf());
				
				getSelf().tell(inMessage, getSender());
			}
		} else if(message instanceof EndProcessMessage) {
			
			EndProcessMessage inMessage = (EndProcessMessage) message;
			final long processId = inMessage.getProcessId();
			cursors.remove(processId);
		} else if (message instanceof RequestTotalSpentTimeMessage) {
			TotalSpentTimeMessage outMessage = new TotalSpentTimeMessage(totalSpentCPUTime);
			getSender().tell(outMessage, getSelf());
			
		}
		
		final long endTime = threadMXBean.getCurrentThreadCpuTime();
		totalSpentCPUTime = totalSpentCPUTime.add(BigInteger.valueOf(endTime - startTime));
	}

	private Collection<String> nextPasswordChunk(Long processId) {
		final List<String> result = new ArrayList<>(PASSWORD_CHUNK_SIZE);
		Cursor cursor = cursors.get(processId);
		switch(cursor.lastPasswordSource) {
		case FILE :
			URL url = this.getClass().getClassLoader().getResource(PASSWORD_FILE_NAME);
			try(RandomAccessFile raf = new RandomAccessFile(url.getPath(), "r")) {
				final long fileLength = raf.length();
				raf.seek(cursor.lastFilePointer);
				int positionInChunk = 0;
				do {
					result.add(raf.readLine());
				} while(raf.getFilePointer() < fileLength && ++positionInChunk < PASSWORD_CHUNK_SIZE);
				cursor.lastFilePointer = raf.getFilePointer();
				if(result.size() < PASSWORD_CHUNK_SIZE || cursor.lastFilePointer == fileLength) {
					//TODO switch to feeding from external sources
					cursor.hasNext = false;
				}
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			break;
		case EXTERNAL :
			throw new UnsupportedOperationException("Not implemented");
		default :
			throw new UnsupportedOperationException("Not implemented");
		}
		
		return result;
	}
	
	private void initCursor(Long processId) {
		Cursor cursor = new Cursor();
		cursor.lastPasswordSource = PasswordSource.FILE;
		cursor.hasNext = true;
		cursor.lastFilePointer = 0;
		cursors.put(processId, cursor);
	}
	
	private boolean processNotEnded(Long processId) {
		return cursors.containsKey(processId);
	}
	
	private boolean morePasswordsAvailable(Long processId) {
		Cursor cursor = cursors.get(processId);
		return cursor.hasNext;
	}

//	/**
//	 * FIXME This method should feed from an external password database somehow. At this moment, it simply generates
//	 * random printable character sequences with a maximum length of 50.
//	 * 
//	 * @param chunkSize
//	 * @return List<String> containing a number of random Strings equal to chunkSize
//	 */
//	private static List<String> generateNewChunkOfPasswords(int chunkSize) {
//		Random random = new Random();
//		byte[] b = new byte[random.nextInt(50)];
//		List<String> list = new ArrayList<String>(chunkSize);
//		final String acceptedPasswordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_- ";
//		final int maxPasswordLength = 50;
//
//		for (int i = 0; i < chunkSize; i++) {
//			int passwordLength = random.nextInt(maxPasswordLength);
//			StringBuilder password = new StringBuilder(passwordLength);
//			for (int j = 0; j < passwordLength; j++) {
//				int charIndex = random.nextInt(acceptedPasswordChars.length());
//				password.append(acceptedPasswordChars.charAt(charIndex));
//			}
//			list.add(password.toString());
//		}
//		list.add("pass");
//		return list;
//	}
	
	/**
	 * The Cursor class is intended to keep track of where we are at every moment with the password chunk
	 * generation for a specific process.
	 * 
	 * @author ddoboga
	 */
	private class Cursor {
		private PasswordSource lastPasswordSource;
		private String lastPassword;
		private long lastFilePointer;
		private boolean hasNext;
		//TODO see what info is needed to keep a cursor for external passwords.
	}

	private static enum PasswordSource {
		FILE, EXTERNAL
	}

}

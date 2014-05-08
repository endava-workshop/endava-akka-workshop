//package akka.ws.pass.breaker.actors;
//
//import akka.actor.UntypedActor;
//import akka.event.Logging;
//import akka.event.LoggingAdapter;
//import akka.ws.pass.breaker.messages.ContinuePasswordFlowMessage;
//import akka.ws.pass.breaker.messages.EndProcessMessage;
//import akka.ws.pass.breaker.messages.PasswordChunkMessage;
//import akka.ws.pass.breaker.messages.RequestPasswordFlowMessage;
//import akka.ws.pass.breaker.messages.RequestTotalSpentTimeMessage;
//import akka.ws.pass.breaker.messages.TotalSpentTimeMessage;
//import akka.ws.pass.rest.RestClient;
//
//import java.lang.management.ManagementFactory;
//import java.lang.management.ThreadMXBean;
//import java.math.BigInteger;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * The PasswordProvider class is intended to sequentially provide password chunks aquired from different sources.
// * 
// * @author Daniel DOBOGA
// */
//public class PasswordProvider extends UntypedActor {
//
//	final static int PASSWORD_CHUNK_SIZE = 1000;
//
//	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
//	
//	private Map<Long, Cursor> cursors = new HashMap<>();
//	
//	private ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
//	
//	private BigInteger totalSpentCPUTime = BigInteger.ZERO;
//
//	public void onReceive(Object message) throws Exception {
//		final long startTime = threadMXBean.getCurrentThreadCpuTime();
//		if(log.isInfoEnabled()) {
//			log.info("\n\n************* PasswordProvider received " + message + "\n*************************\n\n");
//		}
//
//		if (message instanceof RequestPasswordFlowMessage) {
//
//			RequestPasswordFlowMessage inMessage = (RequestPasswordFlowMessage) message;
//			final long processId = inMessage.getProcessId();
//			initCursor(processId);
//			
//			ContinuePasswordFlowMessage outMessage = new ContinuePasswordFlowMessage(processId);
//			getSelf().tell(outMessage, getSender());
//
//		} else if(message instanceof ContinuePasswordFlowMessage) {
//			
//			ContinuePasswordFlowMessage inMessage = (ContinuePasswordFlowMessage) message;
//			final long processId = inMessage.getProcessId();
//			if(processNotEnded(processId) && morePasswordsAvailable(processId)) {
//				Collection<String> passwordChunk = nextPasswordChunk(processId);
//				PasswordChunkMessage outMessage = new PasswordChunkMessage(processId, passwordChunk);
//				getSender().tell(outMessage, getSelf());
//				getSelf().tell(inMessage, getSender());
//				
//				if(passwordChunk.size() < PASSWORD_CHUNK_SIZE) {
//					cursors.get(processId).hasNext = false;
//				}
//			}
//		} else if(message instanceof EndProcessMessage) {
//			
//			EndProcessMessage inMessage = (EndProcessMessage) message;
//			final long processId = inMessage.getProcessId();
//			cursors.remove(processId);
//		} else if (message instanceof RequestTotalSpentTimeMessage) {
//			TotalSpentTimeMessage outMessage = new TotalSpentTimeMessage(totalSpentCPUTime);
//			getSender().tell(outMessage, getSelf());
//			
//		}
//		
//		final long endTime = threadMXBean.getCurrentThreadCpuTime();
//		totalSpentCPUTime = totalSpentCPUTime.add(BigInteger.valueOf(endTime - startTime));
//	}
//
//	private Collection<String> nextPasswordChunk(Long processId) {
//		Cursor cursor = cursors.get(processId);
//		List<String> passwordChunk = RestClient.getPasswords(getContext().system(), cursor.lastPageNum ++, PASSWORD_CHUNK_SIZE);
//
//		return passwordChunk;
//	}
//	
//	private void initCursor(Long processId) {
//		Cursor cursor = new Cursor();
//		cursor.hasNext = true;
//		cursor.lastPageNum = 0;
//		cursors.put(processId, cursor);
//	}
//	
//	private boolean processNotEnded(Long processId) {
//		return cursors.containsKey(processId);
//	}
//	
//	private boolean morePasswordsAvailable(Long processId) {
//		Cursor cursor = cursors.get(processId);
//		return cursor.hasNext;
//	}
//	
//	/**
//	 * The Cursor class is intended to keep track of where we are at every moment with the password chunk
//	 * generation for a specific process.
//	 * 
//	 * @author ddoboga
//	 */
//	private class Cursor {
//		private int lastPageNum;
//		private boolean hasNext;
//	}
//
//}

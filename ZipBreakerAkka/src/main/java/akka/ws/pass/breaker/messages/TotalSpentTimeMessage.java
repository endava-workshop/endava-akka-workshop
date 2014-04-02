package akka.ws.pass.breaker.messages;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

/**
 * The RequestTotalSpentTimeMessage class represents the message to be sent by the PasswordChecker actor
 * to communicate the total CPU time it has spent in processing since it has started.
 * 
 * @author Daniel DOBOGA
 */
public class TotalSpentTimeMessage {

	private static final long serialVersionUID = 6657448564691516087L;
	
	private BigInteger totalCPUTimeNano;

	public TotalSpentTimeMessage(BigInteger totalCPUTimeNano) {
		this.totalCPUTimeNano = totalCPUTimeNano;
	}

	public BigInteger getTotalCPUTimeNano() {
		return totalCPUTimeNano;
	}

	public long getTotalCPUTimeMillis() {
		return TimeUnit.MILLISECONDS.convert(totalCPUTimeNano.longValue(), TimeUnit.NANOSECONDS);
	}

}

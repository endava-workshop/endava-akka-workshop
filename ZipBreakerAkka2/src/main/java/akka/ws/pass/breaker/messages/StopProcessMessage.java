package akka.ws.pass.breaker.messages;

import java.io.Serializable;

public class StopProcessMessage implements Serializable {

	private static final long serialVersionUID = 5181024853617874035L;

	private long processId;

	public long getProcessId() {
		return processId;
	}

	public StopProcessMessage(long processId) {
		super();
		this.processId = processId;
	}
	
}

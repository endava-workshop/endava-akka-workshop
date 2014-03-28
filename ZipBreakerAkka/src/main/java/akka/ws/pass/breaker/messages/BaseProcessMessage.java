package akka.ws.pass.breaker.messages;

import java.io.Serializable;

/**
 * The BaseProcessMessage class is intended to be extended by all messages that are linked to a zip breaking process.
 * 
 * @author ddoboga
 */
public abstract class BaseProcessMessage implements Serializable {

	private static final long serialVersionUID = 6522493032679179305L;

	private long processId;

	public BaseProcessMessage(long processId) {
		super();
		this.processId = processId;
	}

	public long getProcessId() {
		return processId;
	}
}

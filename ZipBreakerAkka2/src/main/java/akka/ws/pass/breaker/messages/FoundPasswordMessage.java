package akka.ws.pass.breaker.messages;

import java.io.Serializable;

/**
 * 
 * 
 * @author ddoboga
 *
 */
public class FoundPasswordMessage implements Serializable {
	
	private static final long serialVersionUID = 7229151074883568111L;
	
	private long processId;
	private String successfullPassword;

	public String getSuccessfullPassword() {
		return successfullPassword;
	}

	public long getProcessId() {
		return processId;
	}

	public FoundPasswordMessage(long processId, String successfullPassword) {
		this.processId = processId;
		this.successfullPassword = successfullPassword;
	}
}

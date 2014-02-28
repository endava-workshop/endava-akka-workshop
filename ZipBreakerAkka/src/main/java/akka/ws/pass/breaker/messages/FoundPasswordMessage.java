package akka.ws.pass.breaker.messages;

import java.io.Serializable;

public class FoundPasswordMessage implements Serializable {

	private long processId;

	private String successfullPassword;

	public long getProcessId() {
		return processId;
	}

	public String getSuccessfullPassword() {
		return successfullPassword;
	}

	public FoundPasswordMessage(long processId, String successfullPassword) {
		super();
		this.processId = processId;
		this.successfullPassword = successfullPassword;
	}
}

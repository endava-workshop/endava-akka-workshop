package akka.ws.test.messages;

public class BrokenSuccessMessage {

	private long processId;

	private String successfullPassword;

	public long getProcessId() {
		return processId;
	}

	public String getSuccessfullPassword() {
		return successfullPassword;
	}

	public BrokenSuccessMessage(long processId, String successfullPassword) {
		super();
		this.processId = processId;
		this.successfullPassword = successfullPassword;
	}
}

package akka.ws.pass.breaker.messages;

/**
 * 
 * The FoundPasswordMessage class
 * 
 * @author ddoboga
 *
 */
public class FoundPasswordMessage extends BaseProcessMessage {

	private static final long serialVersionUID = -4011471800774897618L;

	private String successfullPassword;

	public String getSuccessfullPassword() {
		return successfullPassword;
	}

	public FoundPasswordMessage(long processId, String successfullPassword) {
		super(processId);
		this.successfullPassword = successfullPassword;
	}
}

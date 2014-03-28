package akka.ws.pass.breaker.messages;

/**
 * The ContinuePasswordFlowMessage class represents the message to be sent by the PasswordProvider to itself, in order
 * to avoid a loop inside its onReceive method (which would give new messages, such as EndProcessMessage, no chance to
 * be processed).
 * 
 * @author ddoboga
 */
public class ContinuePasswordFlowMessage extends BaseProcessMessage {

	private static final long serialVersionUID = 2148228133538193324L;

	public ContinuePasswordFlowMessage(long processId) {
		super(processId);
	}

}

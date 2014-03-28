package akka.ws.pass.breaker.messages;

/**
 * 
 * The RequestPasswordFlowMessage class 
 * 
 * @author ddoboga
 *
 */
public class RequestPasswordFlowMessage extends BaseProcessMessage {

	private static final long serialVersionUID = 2148228133538193324L;

	public RequestPasswordFlowMessage(long processId) {
		super(processId);
	}

}

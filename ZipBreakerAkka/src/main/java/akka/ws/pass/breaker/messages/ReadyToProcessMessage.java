package akka.ws.pass.breaker.messages;

/**
 * 
 * The ReadyToProcessMessage class 
 * 
 * @author ddoboga
 *
 */
public class ReadyToProcessMessage extends BaseProcessMessage {

	private static final long serialVersionUID = -7775039178132840446L;

	public ReadyToProcessMessage(long processId) {
		super(processId);
	}

}

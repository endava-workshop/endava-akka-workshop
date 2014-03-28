package akka.ws.pass.breaker.messages;

/**
 * The EndProcessMessage class represents the message to be sent by the ZipPasswordBreaker to the workers signalling one
 * of the worker has found the password, thus they should stop working for that process ID.
 * 
 * @author Daniel DOBOGA
 */
public class EndProcessMessage extends BaseProcessMessage {

	private static final long serialVersionUID = -4543948108784406908L;

	public EndProcessMessage(long idProcess) {
		super(idProcess);
	}

}

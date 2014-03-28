package akka.ws.pass.breaker.messages;

/**
 * The StartFeedingProcessMessage class
 * 
 * @author Daniel DOBOGA
 */
public class StartFeedingProcessMessage extends BaseProcessMessage {

	private static final long serialVersionUID = 3653523362624963866L;

	public StartFeedingProcessMessage(long idProcess) {
		super(idProcess);
	}

}

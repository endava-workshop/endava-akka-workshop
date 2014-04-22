package akka.ws.pass.breaker.messages;

/**
 * The ReadyToProcessMessage class represents the message to be transmitted by the ZipPasswordBreakWorker to the
 * WorkDispatcher telling the remote machine coordinated by this ZipPasswordBreakWorker has finished downloading a local
 * copy of the zip file and is ready to process password chunks for the zip associated with the processId contained by
 * this message.
 * 
 * @author ddoboga
 */
public class ReadyToProcessMessage extends BaseProcessMessage {

	private static final long serialVersionUID = -7775039178132840446L;

	public ReadyToProcessMessage(long processId) {
		super(processId);
	}

}

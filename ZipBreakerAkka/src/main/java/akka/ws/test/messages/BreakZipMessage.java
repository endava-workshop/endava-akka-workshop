package akka.ws.test.messages;

/**
 * 
 * The StartProcessMessage class represents the message that tells the ProcessorActor to start the entire workflow.
 * 
 * @author Daniel Doboga
 *
 */
public class BreakZipMessage {
	
	private String zipFilePath;
	private int numberOfWorkers;

	public BreakZipMessage(String zipFilePath, int numberOfWorkers) {
		super();
		this.zipFilePath = zipFilePath;
		this.numberOfWorkers = numberOfWorkers;
	}

	public String getZipFilePath() {
		return zipFilePath;
	}

	public int getNumberOfWorkers() {
		return numberOfWorkers;
	}
}

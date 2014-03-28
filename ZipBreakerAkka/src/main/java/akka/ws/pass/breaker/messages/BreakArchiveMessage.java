package akka.ws.pass.breaker.messages;

import java.io.Serializable;

/**
 * 
 * The BreakZipMessage class represents the message that tells the ZipBreakerActor to start the entire workflow for a zip file.
 * 
 * @author Daniel Doboga
 *
 */
public class BreakArchiveMessage implements Serializable {

	private static final long serialVersionUID = -2987201959668563366L;
	private String zipFilePath;

	public BreakArchiveMessage(String zipFilePath, int numberOfWorkers) {
		super();
		this.zipFilePath = zipFilePath;
	}

	public String getZipFilePath() {
		return zipFilePath;
	}
}

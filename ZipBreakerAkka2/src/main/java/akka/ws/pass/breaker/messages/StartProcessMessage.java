package akka.ws.pass.breaker.messages;

import java.io.Serializable;

/**
 * 
 * @author Daniel Doboga
 *
 */
public class StartProcessMessage implements Serializable {

	private static final long serialVersionUID = -3839261729017147844L;
	private String zipFilePath;

	public StartProcessMessage(String zipFilePath) {
		super();
		this.zipFilePath = zipFilePath;
	}

	public String getZipFilePath() {
		return zipFilePath;
	}
}

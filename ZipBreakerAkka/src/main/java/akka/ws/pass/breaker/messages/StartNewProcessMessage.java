package akka.ws.pass.breaker.messages;

import java.io.File;

/**
 * 
 * The StartNewProcessMessage class 
 * 
 * @author ddoboga
 *
 */
public class StartNewProcessMessage extends BaseProcessMessage {

	private static final long serialVersionUID = -2711780624760449431L;
	
	private File zipFile;

	public StartNewProcessMessage(long processId, File zipFile) {
		super(processId);
	}

	public File getZipFile() {
		return zipFile;
	}

}

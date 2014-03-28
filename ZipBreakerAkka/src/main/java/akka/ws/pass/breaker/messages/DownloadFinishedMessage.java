package akka.ws.pass.breaker.messages;

import java.io.File;

/**
 * The DownloadFinishedMessage class represents the message to be sent by the ZipFileDownlader to the
 * ZipPasswordBreakWorker actor when it finishes downloading the archive from the received URL.
 * 
 * @author Daniel DOBOGA
 */
public class DownloadFinishedMessage extends BaseProcessMessage {

	private static final long serialVersionUID = -4543948108784406908L;
	
	private File zipFile;

	public DownloadFinishedMessage(long idProcess, File zipFile) {
		super(idProcess);
		this.zipFile = zipFile;
	}
	
	public File getZipFile() {
		return zipFile;
	}

}

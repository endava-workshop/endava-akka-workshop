package akka.ws.pass.breaker.messages;

import net.lingala.zip4j.core.ZipFile;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * The NewProcessMessage class represents the message sent by the ZipPasswordBreaker to the workers for starting a new attack against a zip file.
 * 
 * @author Daniel DOBOGA
 *
 */
public class NewProcessMessage implements Serializable {

	private long idProcess;

	private ZipFile zipFile;

	public NewProcessMessage(long idProcess, ZipFile zipFile) {
		super();
		this.idProcess = idProcess;
		this.zipFile = zipFile;
	}

	public long getIdProcess() {
		return idProcess;
	}

	public ZipFile getZipFile() {
		return zipFile;
	}

}

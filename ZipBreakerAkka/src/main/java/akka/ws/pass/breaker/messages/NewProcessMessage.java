package akka.ws.pass.breaker.messages;

import akka.actor.ActorRef;

import java.io.Serializable;
import java.net.URL;

/**
 * 
 * The NewProcessMessage class represents the message sent by the ZipPasswordBreaker to the workers for starting a new attack against a zip file.
 * 
 * @author Daniel DOBOGA
 *
 */
public class NewProcessMessage implements Serializable {

	private static final long serialVersionUID = -3299471703222360216L;

	private long idProcess;

	private URL fileURL;

	public NewProcessMessage(long idProcess, URL fileUrl) {
		super();
		this.idProcess = idProcess;
		this.fileURL = fileUrl;
	}

	public long getIdProcess() {
		return idProcess;
	}

	public URL getFileURL() {
		return fileURL;
	}

}

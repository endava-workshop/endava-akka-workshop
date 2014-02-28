package akka.ws.pass.breaker.messages;

import java.io.Serializable;
import java.util.List;

/**
 * The FeedProcessMessage class represents a message to be sent by the ZipPasswordBreaker to the workers, constantly
 * feeding the attack with new chunks of passwords to be checked.
 * 
 * @author ddoboga
 */
public class FeedProcessMessage implements Serializable {

	private List<String> passwords;

	private long idProcess;

	public FeedProcessMessage(List<String> passwords, long idProcess) {
		super();
		this.passwords = passwords;
		this.idProcess = idProcess;
	}

	public List<String> getPasswords() {
		return passwords;
	}

	public long getIdProcess() {
		return idProcess;
	}

}

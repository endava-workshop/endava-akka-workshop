package akka.ws.pass.breaker.messages;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

/**
 * The FeedProcessMessage class represents a message to be sent by the ZipPasswordBreaker to the workers, constantly
 * feeding the attack with new chunks of passwords to be checked.
 * 
 * @author ddoboga
 */
public class FeedProcessMessage extends BaseProcessMessage {

	private static final long serialVersionUID = 6503801934483367773L;

	private Collection<String> passwords;

	public FeedProcessMessage(Collection<String> passwords, long idProcess) {
		super(idProcess);
		this.passwords = ImmutableSet.copyOf(passwords);
	}

	public Collection<String> getPasswords() {
		return passwords;
	}

}

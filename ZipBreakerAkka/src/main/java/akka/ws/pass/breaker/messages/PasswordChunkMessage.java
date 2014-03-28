package akka.ws.pass.breaker.messages;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * The PasswordChunkMessage class 
 * 
 * @author ddoboga
 *
 */
public class PasswordChunkMessage extends BaseProcessMessage {

	private static final long serialVersionUID = -8713231994812171121L;
	
	private Collection<String> passwordChunk;

	public PasswordChunkMessage(long processId, Collection<String> passwordChunk) {
		super(processId);
		
		this.passwordChunk = ImmutableSet.copyOf(passwordChunk);
		//this overhead may not be necessary; however, we'll see when performance testing...
	}

	public Collection<String> getPasswordChunk() {
		return passwordChunk;
	}

}

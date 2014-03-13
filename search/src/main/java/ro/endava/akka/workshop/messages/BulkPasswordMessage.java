package ro.endava.akka.workshop.messages;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by cosmin on 3/13/14.
 * Class to use for a bulk request, when indexing multiple passwords
 */
public class BulkPasswordMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<PasswordMessage> passwords;

    public BulkPasswordMessage(List<PasswordMessage> passwords) {
        this.passwords = Collections.unmodifiableList(passwords);
    }

    public List<PasswordMessage> getPasswords() {
        return passwords;
    }

    @Override
    public String toString() {
        return "BulkPasswordMessage{" +
                "passwords=" + passwords +
                '}';
    }
}

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

    //private PasswordType passwordType;

    public BulkPasswordMessage(List<PasswordMessage> passwords/*, PasswordType passwordType*/) {
        this.passwords = Collections.unmodifiableList(passwords);
        //this.passwordType = passwordType;
    }

    public List<PasswordMessage> getPasswords() {
        return passwords;
    }

//    public PasswordType getPasswordType() {
//        return passwordType;
//    }

    @Override
    public String toString() {
        return "BulkPasswordMessage{" +
                "passwords=" + passwords +
                '}';
    }
}

package ro.endava.akka.workshop.messages;

import java.io.Serializable;

/**
 * Created by cosmin on 3/13/14.
 * Class representing the password that will get indexed.
 * It holds the actual password, which will be id of the document for unique constraints
 */
public class PasswordMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String password;

    public PasswordMessage(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "PasswordMessage{" +
                "password='" + password + '\'' +
                '}';
    }
}

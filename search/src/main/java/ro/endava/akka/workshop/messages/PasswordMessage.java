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
    private long indexedDate;

    public PasswordMessage(String password, long indexedDate) {
        this.password = password;
        this.indexedDate = indexedDate;
    }

    public String getPassword() {
        return password;
    }

    public long getIndexedDate() {
        return indexedDate;
    }

    @Override
    public String toString() {
        return "PasswordMessage{" +
                "password='" + password + '\'' +
                '}';
    }
}

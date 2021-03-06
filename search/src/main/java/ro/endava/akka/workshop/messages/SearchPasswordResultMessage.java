package ro.endava.akka.workshop.messages;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by cosmin on 4/9/14.
 */
public class SearchPasswordResultMessage implements Serializable {

    private List<String> passwords;

    public SearchPasswordResultMessage(List<String> passwords) {
        this.passwords = Collections.unmodifiableList(passwords);
    }

    public List<String> getPasswords() {
        return passwords;
    }

}

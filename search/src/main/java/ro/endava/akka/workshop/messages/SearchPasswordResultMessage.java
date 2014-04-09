package ro.endava.akka.workshop.messages;

import akka.actor.ActorRef;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by cosmin on 4/9/14.
 */
public class SearchPasswordResultMessage implements Serializable {

    private List<String> passwords;
    private ActorRef requestor;

    public SearchPasswordResultMessage(ActorRef requestor, List<String> passwords) {
        this.requestor = requestor;
        this.passwords = Collections.unmodifiableList(passwords);
    }

    public List<String> getPasswords() {
        return passwords;
    }

    public ActorRef getRequestor() {
        return requestor;
    }
}

package ro.endava.akka.workshop.messages;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by cosmin on 4/9/14.
 * Class holding the message for searching passwords.
 * Should not have query because we just want to retrieve all passwords
 * Pagination features
 */
public class SearchPasswordReqMessage implements Serializable {
    private Long from;
    private Long size;
    private PasswordType passwordType;
    private ActorRef sender;
    //TODO add sort

    public SearchPasswordReqMessage(PasswordType passwordType, Long from, Long size, ActorRef sender) {
        this.from = from;
        this.size = size;
        this.passwordType = passwordType;
        this.sender = sender;
    }

    public Long getFrom() {
        return from;
    }

    public Long getSize() {
        return size;
    }

    public ActorRef getSender() {
        return sender;
    }

    public PasswordType getPasswordType() {
        return passwordType;
    }

    @Override
    public String toString() {
        return "SearchPasswordMessage{" +
                "from=" + from +
                ", size=" + size +
                ", sender=" + sender +
                '}';
    }
}

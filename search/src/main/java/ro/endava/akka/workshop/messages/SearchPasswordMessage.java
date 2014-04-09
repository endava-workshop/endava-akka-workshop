package ro.endava.akka.workshop.messages;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by cosmin on 4/9/14.
 * Class holding the message for searching passwords.
 * Should not have query because we just want to retrieve all passwords
 * Pagination features
 */
public class SearchPasswordMessage implements Serializable {
    private Long from;
    private Long size;
    private ActorRef sender;

    //TODO add sort

    public SearchPasswordMessage(ActorRef sender) {
        this.sender = sender;
    }

    public SearchPasswordMessage(ActorRef sender, Long from, Long size) {
        this.sender = sender;
        this.from = from;
        this.size = size;
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

    @Override
    public String toString() {
        return "SearchPasswordMessage{" +
                "from=" + from +
                ", size=" + size +
                ", sender=" + sender +
                '}';
    }
}

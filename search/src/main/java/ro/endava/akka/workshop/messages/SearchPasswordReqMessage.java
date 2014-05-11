package ro.endava.akka.workshop.messages;

import akka.actor.ActorRef;
import ro.endava.akka.workshop.es.actions.structures.ESSort;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cosmin on 4/9/14.
 * Class holding the message for searching passwords.
 * Should not have query because we just want to retrieve all passwords
 * Pagination features
 */
public class SearchPasswordReqMessage implements Serializable {
    private Long from;
    private Long size;
    private List<ESSort> sort;
    private ActorRef sender;

    public SearchPasswordReqMessage(Long from, Long size, List<ESSort> sort, ActorRef sender) {
        this.from = from;
        this.size = size;
        this.sort = sort;
        this.sender = sender;
    }

    public SearchPasswordReqMessage(Long from, Long size, ActorRef sender) {
        this.from = from;
        this.size = size;
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

    public List<ESSort> getSort() {
        return sort;
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

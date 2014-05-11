package ro.endava.akka.workshop.messages;

import ro.endava.akka.workshop.es.actions.structures.ESSort;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cosmin on 4/9/14.
 * Class holding the message for searching passwords.
 * Should not have query because we just want to retrieve all passwords
 * Pagination features
 */
public class SearchPasswordMessage implements Serializable {
    private Long from;
    private Long size;
    private List<ESSort> sort;

    public SearchPasswordMessage() {
    }

    public SearchPasswordMessage(Long from, Long size) {
        this.from = from;
        this.size = size;
    }

    public SearchPasswordMessage(Long from, Long size, List<ESSort> sort) {
        this.from = from;
        this.size = size;
        this.sort = sort;
    }

    public Long getFrom() {
        return from;
    }

    public Long getSize() {
        return size;
    }

    public List<ESSort> getSort() {
        return sort;
    }
}

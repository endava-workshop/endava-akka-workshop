package ro.endava.akka.workshop.messages;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by cosmin on 3/10/14.
 * Class to use for a bulk request, when indexing multiple articles
 */
public class BulkIndexMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<IndexMessage> indexMessages;

    public BulkIndexMessage(List<IndexMessage> indexMessages) {
        this.indexMessages = Collections.unmodifiableList(indexMessages);
    }

    public List<IndexMessage> getIndexMessages() {
        return indexMessages;
    }

    @Override
    public String toString() {
        return "BulkIndexMessage{" +
                "indexMessages=" + indexMessages +
                '}';
    }
}

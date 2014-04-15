package ro.endava.akka.workshop.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.endava.akka.workshop.es.actions.ESIndexAction;
import ro.endava.akka.workshop.es.client.ESRestClient;
import ro.endava.akka.workshop.es.client.ESRestClientFactory;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.IndexMessage;
import akka.actor.UntypedActor;


/**
 * Created by cosmin on 3/10/14.
 * Actor that will index articles in ES server
 */
public class IndexArticleActor extends UntypedActor {
    private final static Logger LOGGER = LoggerFactory.getLogger(IndexArticleActor.class);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof IndexMessage) {
            indexArticle((IndexMessage) message);
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }

    /**
     * Indexing the article in the ES server
     *
     * @param indexMessage
     */
    private void indexArticle(IndexMessage indexMessage) {
        ESRestClientFactory factory = new ESRestClientFactory();
        ESRestClient client = factory.getClient(ESRestClientFactory.Type.ASYNC, false);

        ESIndexAction indexAction = new ESIndexAction.Builder().index("articles").
                type("article").body(indexMessage).build();

        client.executeAsyncBlocking(indexAction);
    }
}

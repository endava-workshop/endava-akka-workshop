package ro.endava.akka.workshop.actors;


import ro.endava.akka.workshop.es.actions.ESIndexAction;
import ro.endava.akka.workshop.es.client.ESRestClient;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.IndexMessage;
import akka.actor.UntypedActor;


/**
 * Created by cosmin on 3/10/14.
 * Actor that will index articles in ES server
 */
public class IndexArticleActor extends UntypedActor {

    private ESRestClient client;

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
        ESIndexAction indexAction = new ESIndexAction.Builder().index("articles").
                type("article").body(indexMessage).build();

        client.executeAsyncBlocking(indexAction);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        client = ESRestClient.getInstance();
    }
}

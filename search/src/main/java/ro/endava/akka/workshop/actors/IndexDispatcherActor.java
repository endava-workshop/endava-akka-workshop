package ro.endava.akka.workshop.actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.BulkIndexMessage;
import ro.endava.akka.workshop.messages.IndexMessage;

/**
 * Created by cosmin on 3/10/14.
 * Dispatcher actor which receives the message with the payload to index.
 * Will forward the message to both IndexArticleActor and IndexTokenizerActor
 */
public class IndexDispatcherActor extends UntypedActor {

    private final static Logger LOGGER = LoggerFactory.getLogger(IndexDispatcherActor.class);

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof IndexMessage) {
            IndexMessage indexMessage = (IndexMessage) message;
            LOGGER.info("Index Dispatcher Actor received a message: " + indexMessage);
            this.getContext().actorOf(Props.create(IndexArticleActor.class)).tell(indexMessage, getSelf());
            this.getContext().actorOf(Props.create(IndexTokenizerActor.class)).tell(indexMessage, getSelf());
        } else {
            if (message instanceof BulkIndexMessage) {
                throw new ApplicationException("Not yet implemented.", ErrorCode.NOT_IMPLEMENTED);
            } else {
                throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
            }
        }
    }
}

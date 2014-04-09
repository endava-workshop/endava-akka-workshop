package ro.endava.akka.workshop.actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.IndexMessage;
import ro.endava.akka.workshop.messages.SearchPasswordMessage;
import ro.endava.akka.workshop.messages.SearchPasswordResultMessage;

/**
 * Created by cosmin on 3/10/14.
 * Router actor which receives the messages for searching.
 * Will forward the message to either SearchArticleActor and SearchPasswordActor
 */
public class SearchRouterActor extends UntypedActor {

    private final static Logger LOGGER = LoggerFactory.getLogger(SearchRouterActor.class);

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof SearchPasswordMessage) {
            SearchPasswordMessage searchPasswordMessage = (SearchPasswordMessage) message;
            LOGGER.info("Search Router Actor received a message: " + searchPasswordMessage);
            this.getContext().actorOf(Props.create(SearchPasswordActor.class)).tell(searchPasswordMessage, getSelf());
        } else {
            if (message instanceof SearchPasswordResultMessage) {
                SearchPasswordResultMessage passwordResultMessage = (SearchPasswordResultMessage) message;
                LOGGER.info("Search Router Actor received a result for password searching: " + passwordResultMessage);
                passwordResultMessage.getRequestor().tell(passwordResultMessage, getSelf());
            } else {
                throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
            }
        }
    }
}

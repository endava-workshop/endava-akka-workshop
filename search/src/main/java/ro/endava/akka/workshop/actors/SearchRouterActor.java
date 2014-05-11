package ro.endava.akka.workshop.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.endava.akka.workshop.es.actions.structures.ESSort;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.SearchPasswordHitsMessage;
import ro.endava.akka.workshop.messages.SearchPasswordMessage;
import ro.endava.akka.workshop.messages.SearchPasswordReqMessage;
import ro.endava.akka.workshop.messages.SearchPasswordResultMessage;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cosmin on 3/10/14.
 * Router actor which receives the messages for searching.
 * Will forward the message to either SearchArticleActor and SearchPasswordActor
 */
public class SearchRouterActor extends UntypedActor {

    public static AtomicInteger sentChunks = new AtomicInteger(0);
    public static AtomicInteger indexedChunks = new AtomicInteger(0);

    private final static Logger LOGGER = LoggerFactory.getLogger(SearchRouterActor.class);

    private ActorRef searchPassActorRouter;

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof SearchPasswordMessage) {
            sentChunks.addAndGet(1);
            LOGGER.debug("received request with range starting at " + ((SearchPasswordMessage) message).getFrom() + " no " + sentChunks);
            searchPassActorRouter.tell(getSearchPasswordReqMessage((SearchPasswordMessage) message), getSelf());
        } else if (message instanceof SearchPasswordHitsMessage) {
            indexedChunks.addAndGet(1);
            LOGGER.debug("received response " + indexedChunks);
            SearchPasswordHitsMessage hitsMessage = (SearchPasswordHitsMessage) message;
            hitsMessage.getRequestor().tell(new SearchPasswordResultMessage(hitsMessage.getPasswords()), getSelf());
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        /**
         * Creating actors
         */
        if (searchPassActorRouter == null) {
            searchPassActorRouter = getContext().actorOf(
                    Props.create(SearchPasswordActor.class).withRouter(
                            new RoundRobinRouter(50)));
        }
    }

    private SearchPasswordReqMessage getSearchPasswordReqMessage(SearchPasswordMessage message) {
        long from = 0L;
        long size = 100L;
        List<ESSort> sort = new ArrayList<>();
        sort.add(new ESSort("indexedDate", ESSort.SortType.ASC));

        if (message.getFrom() != null) {
            from = message.getFrom();
        }
        if (message.getSize() != null) {
            size = message.getSize();
        }
        return new SearchPasswordReqMessage(from, size, sort, getSender());
    }
}

package ro.endava.akka.workshop.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.SearchPasswordHitsMessage;
import ro.endava.akka.workshop.messages.SearchPasswordMessage;
import ro.endava.akka.workshop.messages.SearchPasswordReqMessage;
import ro.endava.akka.workshop.messages.SearchPasswordResultMessage;

/**
 * Created by cosmin on 3/10/14.
 * Router actor which receives the messages for searching.
 * Will forward the message to either SearchArticleActor and SearchPasswordActor
 */
public class SearchRouterActor extends UntypedActor {


    private ActorRef searchPassActorRouter;

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof SearchPasswordMessage) {
            SearchPasswordMessage mess = (SearchPasswordMessage) message;
            searchPassActorRouter.tell(new SearchPasswordReqMessage(mess.getPasswordType(), mess.getFrom(), mess.getSize(), getSender()), getSelf());
        } else if (message instanceof SearchPasswordHitsMessage) {
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
}

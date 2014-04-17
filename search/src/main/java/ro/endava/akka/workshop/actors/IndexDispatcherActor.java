package ro.endava.akka.workshop.actors;

import akka.dispatch.Mapper;
import akka.pattern.Patterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.*;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import scala.Option;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.util.Try;

import java.util.concurrent.TimeUnit;

/**
 * Created by cosmin on 3/10/14.
 * Dispatcher actor which handles all index actions in ES
 * Will forward the messages to the corresponding actors
 */
public class IndexDispatcherActor extends UntypedActor {

    private final static Logger LOGGER = LoggerFactory.getLogger(IndexDispatcherActor.class);

    private ActorRef indexPassActorRouter;

    private ActorRef indexArticleActorRouter;

    private ActorRef indexTokenizerActorRouter;

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof IndexMessage) {
            indexArticleActorRouter.tell(message, getSelf());
            indexTokenizerActorRouter.tell(message, getSelf());
        } else if (message instanceof LocalPasswordMessage) {
            this.getContext().actorOf(Props.create(LocalPasswordActor.class)).tell(message, getSelf());
        } else if (message instanceof BulkPasswordMessage) {
            indexPassActorRouter.tell(message, getSelf());
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        /**
         * Creating indices using ask
         */
        ActorRef esAdminActor = this.getContext().actorOf(Props.create(ESAdminActor.class));
        Future<Object> ask = (Future<Object>) Patterns.ask(esAdminActor, new AdminMessage(), 30000);
        Await.ready(ask, Duration.create(30, TimeUnit.SECONDS));
        Option<Try<Object>> value = ask.value();
        Try<AdminResponseMessage> adminResponseMessageTry = value.get().map(new Mapper<Object, AdminResponseMessage>() {
            @Override
            public AdminResponseMessage apply(Object parameter) {
                AdminResponseMessage responseMessage = (AdminResponseMessage) parameter;
                return responseMessage;
            }
        });
        AdminResponseMessage resp = adminResponseMessageTry.get();
        if(resp.getIsOk()){
            LOGGER.info("The indices have been created");
        }
        else{
            LOGGER.info("Error in ESAdminActor...can not continue");
        }
        
        /**
         * Creating actors
         */
        if (indexArticleActorRouter == null) {
            indexArticleActorRouter = getContext().actorOf(
                    Props.create(IndexArticleActor.class).withRouter(
                            new RoundRobinRouter(5)));
        }

        if (indexPassActorRouter == null) {
            indexPassActorRouter = getContext().actorOf(
                    Props.create(IndexPasswordActor.class).withRouter(
                            new RoundRobinRouter(5)));
        }

        if (indexTokenizerActorRouter == null) {
            indexTokenizerActorRouter = getContext().actorOf(
                    Props.create(IndexTokenizerActor.class).withRouter(
                            new RoundRobinRouter(5)));
        }
    }

}

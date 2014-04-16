package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import scala.Option;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

/**
 * Base actor
 *
 * @author Radu Ciumag
 */
public abstract class BaseActor extends UntypedActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private static final FiniteDuration ACTOR_FIND_TIMEOUT = Duration.create(10, TimeUnit.SECONDS);

    /**
     * Get the actor local path
     *
     * @param partialName The actor partial name
     * @return The actor path
     */
    protected String getActorPath(final String partialName) {
        return "akka://" + WebCrawlerConstants.SYSTEM_NAME + "/user/" + WebCrawlerConstants.MASTER_ACTOR_NAME + (null != partialName ? ("/" + partialName) : "");
    }

    /**
     * Find an local actor specified by name and do some work with it
     *
     * @param partialName The partial actor name
     * @param onSuccess   On success action
     * @param onFailure   On failure action
     */
    protected void findLocalActor(final String partialName, final OnSuccess<ActorRef> onSuccess, final OnFailure onFailure) {
        final Future<ActorRef> actorRef = getContext().actorSelection(getActorPath(partialName)).resolveOne(ACTOR_FIND_TIMEOUT);

        actorRef.onSuccess(onSuccess, getContext().dispatcher());
        actorRef.onFailure(onFailure, getContext().dispatcher());
    }

    /**
     * Find an local actor specified by name and return the {@link akka.actor.ActorRef}
     *
     * @param partialName The partial actor name
     * @return The found {@link akka.actor.ActorRef} or {@code null}
     */
    protected ActorRef findLocalActor(final String partialName) throws Exception {
        final Future<ActorRef> actorRefFuture = getContext().actorSelection(getActorPath(partialName)).resolveOne(ACTOR_FIND_TIMEOUT);

        return Await.result(actorRefFuture, ACTOR_FIND_TIMEOUT);
    }

    /**
     * Find an remote actor specified by name and do some work with it
     *
     * @param pathFromUser The partial path from: /user/[pathFromUser]
     * @param onSuccess    On success action
     * @param onFailure    On failure action
     */
    protected void findRemoteActor(final String remoteSystemIdentification, final String pathFromUser, final OnSuccess<ActorRef> onSuccess, final OnFailure onFailure) {
        final Future<ActorRef> actorRef = getContext().actorSelection("akka.tcp://" + remoteSystemIdentification + "/user/" + pathFromUser).resolveOne(
                Duration.create(5, TimeUnit.SECONDS));

        actorRef.onSuccess(onSuccess, getContext().dispatcher());
        actorRef.onFailure(onFailure, getContext().dispatcher());
    }

    /**
     * Process the string and convert it to a valid actor name
     *
     * @param name The original name
     * @return The valid actor name
     */
    protected String getActorName(String name) {
        return name.replace('.', '_').replace(':', '_').replace('/', '_');
    }

    @Override
    public void postStop() throws Exception {
        LOG.error("Actor " + getSelf().getClass() + " will stop");
        super.postStop();
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        LOG.error("Actor " + getSelf().getClass() + " will be restarted." +
                " The reason class[" + reason.getClass() + " message [" + reason.getMessage() + "].");
        reason.printStackTrace();

        super.preRestart(reason, message);
    }


}
package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import org.apache.log4j.Logger;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Base actor
 *
 * @author Radu Ciumag
 */
public abstract class BaseActor extends UntypedActor {
    private static final Logger LOG = Logger.getLogger(BaseActor.class);

    /**
     * Find an actor specified by name and do some work with it
     *
     * @param partialName The partial actor name
     * @param onSuccess   On success action
     * @param onFailure   On failure action
     */
    protected void findActor(String partialName, OnSuccess<ActorRef> onSuccess, OnFailure onFailure) {
        Future<ActorRef> actorRef = getContext().actorSelection("akka://" + WebCrawlerConstants.SYSTEM_NAME + "/user/" + WebCrawlerConstants.MASTER_ACTOR_NAME + "/" +
                partialName).resolveOne(Duration.create(1, TimeUnit.SECONDS));

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
        return name.replace('.', '_');
    }
}
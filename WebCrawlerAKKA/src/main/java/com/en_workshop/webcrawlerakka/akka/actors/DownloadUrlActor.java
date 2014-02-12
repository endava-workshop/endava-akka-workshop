package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.requests.DownloadUrlRequest;
import com.en_workshop.webcrawlerakka.akka.requests.DownloadUrlResponse;
import com.en_workshop.webcrawlerakka.akka.requests.ProcessContentRequest;
import org.apache.log4j.Logger;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * @author Radu Ciumag
 */
public class DownloadUrlActor extends UntypedActor {
    private static final Logger LOG = Logger.getLogger(DownloadUrlActor.class);

    /**
     * {@inheritDoc}
     */
    public void onReceive(Object message) {
        if (message instanceof DownloadUrlRequest) {
            LOG.info("DownloadUrlRequest: " + message);

            final DownloadUrlRequest downloadUrlRequest = (DownloadUrlRequest) message;

            //TODO: Get the url content
            LOG.info("Asking for url: " + downloadUrlRequest.getWebUrl().getUrl());

            //TODO: Check the mine-type
            //TODO: Sent message to processing
            ActorRef processingActor = getContext().actorSelection("akka://" + WebCrawlerConstants.SYSTEM_NAME + "/user/" + WebCrawlerConstants.MASTER_ACTOR_NAME + "/" +
                    WebCrawlerConstants.PROCESSING_MASTER_ACTOR_NAME).anchor();
            processingActor.tell(new ProcessContentRequest(downloadUrlRequest.getWebUrl(), "TODO - content"), getSelf());

            /* Identify the processing master actor and send it the work. */
            Future<ActorRef> processingMasterSearch = getContext().actorSelection("akka://" + WebCrawlerConstants.SYSTEM_NAME + "/user/" + WebCrawlerConstants.MASTER_ACTOR_NAME + "/" +
                    WebCrawlerConstants.PROCESSING_MASTER_ACTOR_NAME).resolveOne(Duration.create(1, TimeUnit.SECONDS));
            processingMasterSearch.onSuccess(new OnSuccess<ActorRef>() {
                @Override
                public void onSuccess(ActorRef processingMasterActor) throws Throwable {
                    processingMasterActor.tell(new ProcessContentRequest(downloadUrlRequest.getWebUrl(), "TODO - content"), getSelf());
                }
            }, getContext().dispatcher());
            processingMasterSearch.onFailure(new OnFailure() {
                @Override
                public void onFailure(Throwable throwable) throws Throwable {
                    LOG.error("Cannot identify the processing master actor. SOMETHING IS WRONG!!!");
                }
            }, getContext().dispatcher());

            /* Report back to the domain actor */
            DownloadUrlResponse response = new DownloadUrlResponse((DownloadUrlRequest) message);
            getSender().tell(response, getSelf());

            //TODO: Start actor to persist url and domain information

            LOG.info("DownloadUrlRequest DONE with message: " + response);
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}
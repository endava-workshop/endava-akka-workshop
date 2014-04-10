package com.en_workshop.webcrawlerakka.akka.actors.processing;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistContentRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Actor that will extract the data and send it to the Persistence Master.
 * <p/>
 * Created by roxana on 3/12/14.
 */
public class DataExtractorActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof ProcessContentRequest) {
            LOG.debug("Data extract: START");

            final ProcessContentRequest processContentRequest = (ProcessContentRequest) message;
            String content = processContentRequest.getContent();

            Document document = Jsoup.parse(content);
            String strippedText = document.body().text();

            findLocalActor(WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                        @Override
                        public void onSuccess(ActorRef persistenceMasterActor) throws Throwable {
                            persistenceMasterActor.tell(new PersistContentRequest(processContentRequest.getSource(), processContentRequest.getContent()), getSelf());
                        }
                    }, new OnFailure() {
                        @Override
                        public void onFailure(Throwable throwable) throws Throwable {
                            LOG.error("Cannot find Persistence Master");
                        }
                    }
            );

            LOG.debug("Data extract: STOP");
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }

    }
}

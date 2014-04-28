package com.en_workshop.webcrawlerakka.akka.actors.processing;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistContentRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddLinkStatisticsRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Actor that will extract the data and send it to the Persistence Master.
 * <p/>
 * Created by roxana on 3/12/14.
 */
public class DataExtractorActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private ActorRef parent;

    public DataExtractorActor(ActorRef parent) {
        this.parent = parent;
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof ProcessContentRequest) {
            LOG.debug("Received content to extract");

            final ProcessContentRequest processContentRequest = (ProcessContentRequest) message;
            String content = processContentRequest.getContent();

            Document document = Jsoup.parse(content);
            if (document == null || document.body() == null) {
                return;
            }
            final String strippedText = document.body().text();

            getParent().tell(new PersistContentRequest(processContentRequest.getSource(), strippedText), getSelf());
            getParent().tell(new AddLinkStatisticsRequest(processContentRequest.getSource().getDomain(), processContentRequest.getSource()), getSelf());

        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }

    }

    public ActorRef getParent() {
        return parent;
    }
}

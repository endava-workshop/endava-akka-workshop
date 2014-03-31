package com.en_workshop.webcrawlerakka.akka.actors.processing;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Actor that will extract the data and send it to the Persistence Master.
 *
 * Created by roxana on 3/12/14.
 */
public class DataExtractorActor extends BaseActor {

    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);


    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof ProcessContentRequest) {
            LOG.debug("Data extract: START");

            ProcessContentRequest processContentRequest = (ProcessContentRequest) message;
            String content = processContentRequest.getContent();

            Document document = Jsoup.parse(content);
            String strippedText = document.body().text();

            //send text to persistence layer
            LOG.info(strippedText);

            LOG.debug("Data extract: STOP");
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }

    }
}

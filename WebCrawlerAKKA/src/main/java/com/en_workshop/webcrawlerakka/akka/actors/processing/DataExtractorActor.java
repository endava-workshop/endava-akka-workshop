package com.en_workshop.webcrawlerakka.akka.actors.processing;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import org.apache.log4j.Logger;

/**
 * Actor that will extract the data and send it to the Persistence Master.
 *
 * Created by roxana on 3/12/14.
 */
public class DataExtractorActor extends UntypedActor {

    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);


    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof ProcessContentRequest) {

        }else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }

    }
}

package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.UntypedActor;
import com.en_workshop.webcrawlerakka.akka.requests.NextLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.NextLinkResponse;
import com.en_workshop.webcrawlerakka.dao.WebUrlDao;
import com.en_workshop.webcrawlerakka.entities.WebUrl;
import org.apache.log4j.Logger;

/**
 * Identify the next link to be crawled for a specific web domain
 *
 * @author Radu Ciumag
 */
public class NextLinkActor extends UntypedActor {
    private static final Logger LOG = Logger.getLogger(NextLinkActor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof NextLinkRequest) {
            LOG.info("NextLinkRequest: " + message);

            NextLinkRequest nextLinkRequest = (NextLinkRequest) message;

            WebUrl webUrl = WebUrlDao.getNextForCrawling(nextLinkRequest.getWebDomain());
            NextLinkResponse response = new NextLinkResponse(nextLinkRequest, webUrl);
            getSender().tell(response, getSelf());

            LOG.info("NextLinkRequest DONE with message: " + response);
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}
package com.en_workshop.webcrawlerakka.akka.actors.persistence;

import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkResponse;
import com.en_workshop.webcrawlerakka.dao.WebUrlDao;
import com.en_workshop.webcrawlerakka.entities.WebUrl;
import org.apache.log4j.Logger;

/**
 * Identify the next link to be crawled for a specific web domain
 *
 * @author Radu Ciumag
 */
public class NextLinkActor extends BaseActor {
    private static final Logger LOG = Logger.getLogger(NextLinkActor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof NextLinkRequest) {
            NextLinkRequest request = (NextLinkRequest) message;

            WebUrl webUrl = WebUrlDao.getNextForCrawling(request.getWebDomain());
            NextLinkResponse response = new NextLinkResponse(request, webUrl);

            LOG.debug("Found next URL for crawling: " + (null == webUrl ? "NONE" : webUrl.getUrl()));

            getSender().tell(response, getSelf());
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}
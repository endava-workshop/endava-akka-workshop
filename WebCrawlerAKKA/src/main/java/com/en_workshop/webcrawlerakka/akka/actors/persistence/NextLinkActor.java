package com.en_workshop.webcrawlerakka.akka.actors.persistence;

import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkResponse;
import com.en_workshop.webcrawlerakka.dao.LinkDao;
import com.en_workshop.webcrawlerakka.entities.Link;
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

            Link link = LinkDao.getNextForCrawling(request.getDomain());
            NextLinkResponse response = new NextLinkResponse(request, link);

            LOG.debug("Found next URL for crawling: " + (null == link ? "NONE" : link.getUrl()));

            getSender().tell(response, getSelf());
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}
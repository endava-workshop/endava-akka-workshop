package com.en_workshop.webcrawlerakka.akka.actors.statistics;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.other.statistics.ShowStatisticsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.statistics.ShowStatisticsResponse;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddDomainRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddLinkRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.DomainLinkStatistics;
import com.en_workshop.webcrawlerakka.enums.LinkStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Actor that collects the statistics.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/7/14
 */
public class StatisticsActor extends BaseActor {

    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private Map<String, DomainLinkStatistics> domainStatistics =  new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof AddDomainRequest) {
            LOG.debug("Received AddDomainRequest [" + ((AddDomainRequest) message).getDomain().getName() + "]");
            addDomainStatistics((AddDomainRequest) message);
        } else if (message instanceof AddLinkRequest) {
            LOG.debug("Received AddLinkRequest [" + ((AddLinkRequest) message).getLink().getUrl() + "/" + ((AddLinkRequest) message).getLink().getStatus() + "]");
            updateDomainLinkStatistics((AddLinkRequest) message);
        } else if (message instanceof ShowStatisticsRequest) {
            LOG.debug("Received ShowStatisticsRequest");
            getSender().tell(new ShowStatisticsResponse((ShowStatisticsRequest) message, new HashMap<>(domainStatistics), printStatistics()), getSelf());
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }

    private void addDomainStatistics(AddDomainRequest domainRequest) {
        Domain domain = domainRequest.getDomain();
            if (domainStatistics.get(domain.getName()) == null) {
                domainStatistics.put(domain.getName(), new DomainLinkStatistics(domain.getName()));
            }
    }

    /**
     * Updates the statistics for the given domain with the information from the given links.
     *
     * @param addLinkRequest the request containing the the link and the domain.
     */
    private void updateDomainLinkStatistics(AddLinkRequest addLinkRequest) {
        String domain = addLinkRequest.getDomain();
        DomainLinkStatistics domainStats = domainStatistics.get(domain);
        if (domainStats == null) {
            //log the fact that there should have been an empty entry for this domain
            domainStats = new DomainLinkStatistics(domain);
                domainStatistics.put(domain, domainStats);
        }
        /* Update the statistics, depending on the status of the link */
        LinkStatus linkStatus = addLinkRequest.getLink().getStatus();
        switch (linkStatus) {
            case NOT_VISITED:
                domainStats.addIdentifiedLinks();
                break;
            case VISITED:
                domainStats.addDownloadedLinks();
                break;
            case FAILED:
                domainStats.addFailedLinks();
                break;
        }

    }

    private String printStatistics() {
        return Arrays.toString(domainStatistics.entrySet().toArray());
    }

}

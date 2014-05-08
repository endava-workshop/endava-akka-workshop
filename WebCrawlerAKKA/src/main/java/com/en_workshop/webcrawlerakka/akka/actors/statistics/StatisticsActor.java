package com.en_workshop.webcrawlerakka.akka.actors.statistics;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.other.statistics.ShowStatisticsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.statistics.ShowStatisticsResponse;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddBulkLinkStatisticsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddDomainStatisticsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddLinkStatisticsRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.DomainLink;
import com.en_workshop.webcrawlerakka.entities.DomainLinkStatistics;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.enums.DomainStatus;
import com.en_workshop.webcrawlerakka.enums.LinkStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private final Object statisticsLock = new Object();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof AddDomainStatisticsRequest) {
            LOG.info("Received AddDomainStatisticsRequest [" + ((AddDomainStatisticsRequest) message).getDomain().getName() + "]");
            addDomainStatistics((AddDomainStatisticsRequest) message);
        } else if (message instanceof AddLinkStatisticsRequest) {
            LOG.info("Received AddLinkStatisticsRequest [" + ((AddLinkStatisticsRequest) message).getLink().getUrl() + "/" + ((AddLinkStatisticsRequest) message).getLink().getStatus() + "]");
            updateDomainLinkStatistics((AddLinkStatisticsRequest) message);
        } else if (message instanceof AddBulkLinkStatisticsRequest) {
            LOG.info("Received AddBulkLinkStatisticsRequest");
            updateBulkDomainLinkStatistics((AddBulkLinkStatisticsRequest) message);
        } else if (message instanceof ShowStatisticsRequest) {
            LOG.info("Received ShowStatisticsRequest");
            getSender().tell(new ShowStatisticsResponse((ShowStatisticsRequest) message, new HashMap<>(domainStatistics), printStatistics()), getSelf());
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }

    private void addDomainStatistics(AddDomainStatisticsRequest domainRequest) {
        Domain domain = domainRequest.getDomain();
        addDomainStatistics(domain.getName(), domain.getDomainStatus());
    }

    private void addDomainStatistics(String domainName, DomainStatus domainStatus) {
        synchronized (statisticsLock) {
            if (domainStatistics.get(domainName) == null) {
                domainStatistics.put(domainName, new DomainLinkStatistics(domainName));
            }

            domainStatistics.get(domainName).setDomainStatus(domainStatus);
        }
    }

    private void addDomainStatistics(String domainName) {
        addDomainStatistics(domainName, DomainStatus.FOUND);
    }

    /**
     * Updates the statistics for the given domain with the information from the given links.
     *
     * @param addLinkStatisticsRequest the request containing the the link and the domain.
     */
    private void updateDomainLinkStatistics(AddLinkStatisticsRequest addLinkStatisticsRequest) {
        addDomainLinkStatistics(addLinkStatisticsRequest.getDomain(), addLinkStatisticsRequest.getLink());
    }

    /**
     * Updates the statistics for the given domains with the information from the given links.
     *
     * @param addBulkLinkStatisticsRequest the request containing the the links and the domains.
     */
    private void updateBulkDomainLinkStatistics(AddBulkLinkStatisticsRequest addBulkLinkStatisticsRequest) {
        List<DomainLink> data = addBulkLinkStatisticsRequest.getDomainLinks();
        for (DomainLink domainLink : data) {
            addDomainLinkStatistics(domainLink.getDomain().getName(), domainLink.getLink());
        }

    }

    private void addDomainLinkStatistics(String domainName, Link link) {
        DomainLinkStatistics domainStats = domainStatistics.get(domainName);
        if (domainStats == null) {
            addDomainStatistics(domainName);
        }
        domainStats = domainStatistics.get(domainName);
        /* Update the statistics, depending on the status of the link */
        LinkStatus linkStatus = link.getStatus();
        synchronized (this) {
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
    }

    private String printStatistics() {
        return Arrays.toString(domainStatistics.entrySet().toArray());
    }

}

package com.en_workshop.webcrawlerakka.akka.actors.processing;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistDomainLinksRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddBulkLinkStatisticsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddLinkStatisticsRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.DomainLink;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.tools.WebClient;
import com.en_workshop.webcrawlerakka.tools.WebContentTools;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Actor that will identify the links and send them to the Persistence Master.
 * <p/>
 * Created by roxana on 3/12/14.
 */
public class IdentifyLinksActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private ActorRef parent;//ProcessingMasterActor

    public IdentifyLinksActor(ActorRef parent) {
        this.parent = parent;
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof ProcessContentRequest) {
            LOG.info("Identify the links in the content.");

            List<DomainLink> pageLinks = new ArrayList<>();

            final ProcessContentRequest contentToBeProcessed = (ProcessContentRequest) message;
            final String baseUrl = contentToBeProcessed.getSource().getUrl();
            Document document = Jsoup.parse(contentToBeProcessed.getContent(), baseUrl);
            Elements links = document.select("a[href]");
            for (Element link : links) {
                final String normalizedLink = WebContentTools.normalizeURLLink(link.attr("abs:href"));
                if (null == normalizedLink || 0 == normalizedLink.length()) {
                    continue;
                }

                addLink(contentToBeProcessed.getSourceDomain(), baseUrl, normalizedLink, pageLinks);
            }

            persistDomainLinks(pageLinks);
            updateStatistics(pageLinks);
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }


    private void addLink(Domain sourceDomain, String baseLink, String foundLink, List<DomainLink> pageLinks) throws MalformedURLException {

            //if the initilal domain is not the same as the domain of the link, persist both domain and link
            if (StringUtils.isNotBlank(foundLink) && WebClient.isValid(foundLink) && WebClient.isProtocolAccepted(foundLink))  {
                URL url = new URL(foundLink);
                String linkDomain = url.getHost();

                LOG.info("Analyzing link: " + foundLink + " and domain " + linkDomain);

                Domain newDomain = null;
                if (!linkDomain.equals(sourceDomain.getName())) {
                    newDomain = new Domain(url.getHost(), WebCrawlerConstants.DOMAIN_DEFAULT_COOLDOWN, 0);
                }

                pageLinks.add(new DomainLink(null == newDomain ? sourceDomain : newDomain, new Link(linkDomain, foundLink, baseLink)));
            } else {
                LOG.debug("Invalid URL received [" + foundLink + "]");
            }

    }

    /**
     * Sends the request to persist the link.
     *
     * @param pageLinks the links found.
     */
    private void persistDomainLinks(List<DomainLink> pageLinks) {
        //call to persist the normalized link
        getParent().tell(new PersistDomainLinksRequest(pageLinks), getSelf());
    }

    private void updateStatistics(List<DomainLink> pageLinks) {
        AddBulkLinkStatisticsRequest addLinkStatisticsRequest = new AddBulkLinkStatisticsRequest(pageLinks);
        getParent().tell(addLinkStatisticsRequest, getSelf());
    }


    public ActorRef getParent() {
        return parent;
    }
}

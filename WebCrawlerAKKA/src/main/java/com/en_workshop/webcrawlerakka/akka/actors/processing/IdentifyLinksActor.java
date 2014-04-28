package com.en_workshop.webcrawlerakka.akka.actors.processing;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.processing.AnalyzeLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddLinkStatisticsRequest;
import com.en_workshop.webcrawlerakka.tools.WebContentTools;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

            final ProcessContentRequest contentToBeProcessed = (ProcessContentRequest) message;
            final String baseUrl = contentToBeProcessed.getSource().getUrl();
            Document document = Jsoup.parse(contentToBeProcessed.getContent(), baseUrl);
            Elements links = document.select("a[href]");
            for (Element link : links) {
                final String normalizedLink = WebContentTools.normalizeURLLink(link.attr("abs:href"));
                if (null == normalizedLink || 0 == normalizedLink.length()) {
                    continue;
                }

                AnalyzeLinkRequest analyzeLinkRequest = new AnalyzeLinkRequest(contentToBeProcessed.getSourceDomain(), baseUrl, normalizedLink);
                getParent().tell(analyzeLinkRequest, getSelf());

                AddLinkStatisticsRequest addLinkStatisticsRequest = new AddLinkStatisticsRequest(contentToBeProcessed.getSource().getDomain(), contentToBeProcessed.getSource());
                getParent().tell(addLinkStatisticsRequest, getSelf()); // TODO what's the point of putting this inside a loop?
            }
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }

    public ActorRef getParent() {
        return parent;
    }
}

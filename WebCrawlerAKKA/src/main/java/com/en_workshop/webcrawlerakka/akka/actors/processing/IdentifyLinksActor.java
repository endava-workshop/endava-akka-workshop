package com.en_workshop.webcrawlerakka.akka.actors.processing;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.processing.AnalyzeLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddLinkRequest;
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


                //call to analyze the normalized link
                findLocalActor(WebCrawlerConstants.PROCESSING_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                            @Override
                            public void onSuccess(ActorRef processingMasterActor) throws Throwable {
                                AnalyzeLinkRequest analyzeLinkRequest = new AnalyzeLinkRequest(contentToBeProcessed.getSource().getDomain(), baseUrl, normalizedLink);
                                processingMasterActor.tell(analyzeLinkRequest, getSelf());
                            }
                        }, new OnFailure() {
                            @Override
                            public void onFailure(Throwable throwable) throws Throwable {
                                LOG.error("Cannot find Processing Master");
                            }
                        }
                );

                /* Report to the statistics actor. */
                findLocalActor(WebCrawlerConstants.STATISTICS_ACTOR_NAME, new OnSuccess<ActorRef>() {
                            @Override
                            public void onSuccess(ActorRef statisticsActor) throws Throwable {
                                AddLinkRequest addLinkRequest = new AddLinkRequest(contentToBeProcessed.getSource().getDomain(), contentToBeProcessed.getSource());
                                statisticsActor.tell(addLinkRequest, getSelf());
                            }
                        }, new OnFailure() {
                            @Override
                            public void onFailure(Throwable throwable) throws Throwable {
                                LOG.error("Cannot find Statistics Actor.");
                            }
                        }
                );
            }

        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }
}

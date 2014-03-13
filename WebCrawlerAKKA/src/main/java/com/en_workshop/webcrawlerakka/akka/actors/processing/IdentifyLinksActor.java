package com.en_workshop.webcrawlerakka.akka.actors.processing;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import com.en_workshop.webcrawlerakka.entities.WebDomain;
import com.en_workshop.webcrawlerakka.entities.WebUrl;
import com.en_workshop.webcrawlerakka.tools.WebContentTools;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Actor that will identify the links and send them to the Persistence Master.
 *
 * Created by roxana on 3/12/14.
 */
public class IdentifyLinksActor extends BaseActor {

    private static final Logger LOG = Logger.getLogger(IdentifyLinksActor.class);


    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof ProcessContentRequest) {
            final ProcessContentRequest contentToBeProcessed = (ProcessContentRequest) message;
            final String baseUrl = contentToBeProcessed.getSource().getUrl();
            Document document = Jsoup.parse(contentToBeProcessed.getContent(), baseUrl);
            Elements links = document.select("a[href]");
            for (Element link : links) {
                final String normalizedLink = WebContentTools.normalizeURLLink(link.data());
                //call to persist the normalized link
                findActor(WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                            @Override
                            public void onSuccess(ActorRef persistenceMasterActor) throws Throwable {
                                persistenceMasterActor.tell(new PersistLinkRequest(new WebUrl(contentToBeProcessed.getSource().getWebDomain(), normalizedLink)), getSelf());
                            }
                        }, new OnFailure() {
                            @Override
                            public void onFailure(Throwable throwable) throws Throwable {
                                LOG.error("Cannot find Persistence Master");
                            }
                        }
                );
            }

        }else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }

    }
}

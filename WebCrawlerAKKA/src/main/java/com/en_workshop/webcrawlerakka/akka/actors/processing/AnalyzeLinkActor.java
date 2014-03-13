package com.en_workshop.webcrawlerakka.akka.actors.processing;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistDomainRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.AnalyzeLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import com.en_workshop.webcrawlerakka.entities.WebDomain;
import com.en_workshop.webcrawlerakka.entities.WebUrl;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sun.org.mozilla.javascript.internal.json.JsonParser;

import java.net.URL;

/**
 * Actor for analyzing the links. If the domain of the link is different than it's own, persist both link and domain.
 *
 * Created by roxana on 3/13/14.
 */
public class AnalyzeLinkActor extends BaseActor {

    private static final Logger LOG = Logger.getLogger(AnalyzeLinkActor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof AnalyzeLinkRequest) {
//            AnalyzeLinkRequest analyzeLinkRequest = (AnalyzeLinkRequest) message;
//
//            String sourceUrl = analyzeLinkRequest.getSourceDomain().getBaseUrl();
//            String link = analyzeLinkRequest.getLink();
//
//            //if the initial domain is not the same as the domain of the link, persist both domain and link
//            URL url = new URL(link);
//            String path = url.getFile().substring(0, url.getFile().lastIndexOf('/'));
//            String linkDomain = url.getProtocol() + "://" + url.getHost() + path;
//
//            WebDomain newWebDomain = null;
//            if (!linkDomain.equals(sourceUrl)) {
//                newWebDomain = new WebDomain(linkDomain, url.getHost(), 20000, 0);
//                persistDomain(newWebDomain);
//            }
//
//            persistLink(newWebDomain == null ? analyzeLinkRequest.getSourceDomain() : newWebDomain, link);

        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }

    }

    /**
     * Finds the PersistenceMasterActor and sends the request to persist the link.
     *
     * @param webDomain the web domain of the link.
     * @param link the link.
     */
    private void persistLink(final WebDomain webDomain, final String link) {
        //call to persist the normalized link
        findActor(WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                    @Override
                    public void onSuccess(ActorRef persistenceMasterActor) throws Throwable {
                        persistenceMasterActor.tell(new PersistLinkRequest(new WebUrl(webDomain, link)), getSelf());
                    }
                }, new OnFailure() {
                    @Override
                    public void onFailure(Throwable throwable) throws Throwable {
                        LOG.error("Cannot find Persistence Master");
                    }
                }
        );
    }

    /**
     * Finds the PersistenceMasterActor and sends the request to persist the web domain.
     *
     * @param webDomain the web domain of the link.
     */
    private void persistDomain(final WebDomain webDomain) {
        //call to persist the normalized link
        findActor(WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                    @Override
                    public void onSuccess(ActorRef persistenceMasterActor) throws Throwable {
                        persistenceMasterActor.tell(new PersistDomainRequest(webDomain), getSelf());
                    }
                }, new OnFailure() {
                    @Override
                    public void onFailure(Throwable throwable) throws Throwable {
                        LOG.error("Cannot find Persistence Master");
                    }
                }
        );
    }
}

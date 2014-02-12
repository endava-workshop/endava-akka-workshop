package com.en_workshop.webcrawlerakka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.en_workshop.webcrawlerakka.akka.actors.MasterActor;
import com.en_workshop.webcrawlerakka.akka.requests.StartMasterRequest;
import com.en_workshop.webcrawlerakka.dao.WebDomainDao;
import com.en_workshop.webcrawlerakka.dao.WebUrlDao;
import com.en_workshop.webcrawlerakka.entities.WebDomain;
import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class WebCrawler {
    private static final Logger LOG = Logger.getLogger(WebCrawler.class);

    public static void main(String[] args) {
        LOG.info("Web crawler starting ...");

        LOG.info("Add sample (test) data ...");
        WebDomain wikiDomain = WebDomainDao.add(new WebDomain("http://ro.wikipedia.org/", "Wikipedia", 5000, 0));
        if (null != wikiDomain) {
            WebUrlDao.add(wikiDomain, wikiDomain.getBaseUrl());
        }

        WebDomain debianDomain = WebDomainDao.add(new WebDomain("https://wiki.debian.org/", "WikiDebian", 7000, 0));
        if (null != debianDomain) {
            WebUrlDao.add(debianDomain, debianDomain.getBaseUrl());
        }
        LOG.info("Add sample (test) data: DONE");

        LOG.info("Actor system initializing ...");
        ActorSystem actorSystem = ActorSystem.create(WebCrawlerConstants.SYSTEM_NAME);

        ActorRef masterActor = actorSystem.actorOf(Props.create(MasterActor.class), WebCrawlerConstants.MASTER_ACTOR_NAME);
        masterActor.tell(new StartMasterRequest(), ActorRef.noSender());
        LOG.info("Actor system initialize: DONE");
    }
}

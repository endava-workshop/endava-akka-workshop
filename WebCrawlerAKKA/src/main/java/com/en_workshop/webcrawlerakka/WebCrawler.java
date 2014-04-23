package com.en_workshop.webcrawlerakka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.en_workshop.webcrawlerakka.akka.actors.other.ControlActor;
import com.en_workshop.webcrawlerakka.akka.actors.other.StatusActor;
import com.en_workshop.webcrawlerakka.akka.actors.statistics.StatisticsActor;
import com.en_workshop.webcrawlerakka.akka.requests.other.control.master.ControlStartMasterRequest;
import com.en_workshop.webcrawlerakka.dao.DomainDao;
import com.en_workshop.webcrawlerakka.dao.LinkDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Radu Ciumag
 */
public class WebCrawler {
    private static final Logger LOG = LoggerFactory.getLogger(WebCrawler.class);

    public static void main(String[] args) {
        LOG.info("Web crawler starting ...");

        LOG.debug("Add sample (test) data ...");
        Domain wikiDomain = DomainDao.add(new Domain("ro.wikipedia.org", 20000, 0));
        if (null != wikiDomain) {
            LinkDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Pagina_principal%C4%83");
        }
//        Domain unresponsiveDomain = DomainDao.add(new Domain("ro.zzzwikipedia.org", 20000, 0));
//        if (null != unresponsiveDomain) {
//            LinkDao.add(unresponsiveDomain, "http://ro.zzzwikipedia.org/wiki/Pagina_principal%C4%83");
//        }

//        Domain debianDomain = WebDomainDao.add(new Domain("wiki.debian.org", 30000, 0));
//        if (null != debianDomain) {
//            WebUrlDao.add(debianDomain, "http://" + debianDomain.getName());
//        }
        LOG.debug("Add sample (test) data: DONE");

        LOG.debug("Actor system initializing ...");
        final ActorSystem actorSystem = ActorSystem.create(WebCrawlerConstants.SYSTEM_NAME);

        final ActorRef controlActor = actorSystem.actorOf(Props.create(ControlActor.class), WebCrawlerConstants.CONTROL_ACTOR_NAME);
        controlActor.tell(new ControlStartMasterRequest(), ActorRef.noSender());

        final ActorRef statusActor = actorSystem.actorOf(Props.create(StatusActor.class), WebCrawlerConstants.STATUS_ACTOR_NAME);
        final ActorRef statisticsActor = actorSystem.actorOf(Props.create(StatisticsActor.class), WebCrawlerConstants.STATISTICS_ACTOR_NAME);

        LOG.debug("Actor system initialize: DONE");

        /* Display the micro console UI */
        WebCrawlerConsole.microConsole(actorSystem, controlActor, statusActor, statisticsActor);
    }
}

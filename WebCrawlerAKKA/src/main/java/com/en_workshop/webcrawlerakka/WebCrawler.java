package com.en_workshop.webcrawlerakka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.en_workshop.webcrawlerakka.akka.actors.other.ControlActor;
import com.en_workshop.webcrawlerakka.akka.actors.other.StatusActor;
import com.en_workshop.webcrawlerakka.akka.actors.persistence.PersistenceActor;
import com.en_workshop.webcrawlerakka.akka.actors.statistics.StatisticsActor;
import com.en_workshop.webcrawlerakka.akka.requests.other.control.master.ControlStartMasterRequest;
import com.en_workshop.webcrawlerakka.dao.DomainDao;
import com.en_workshop.webcrawlerakka.dao.LinkDao;
import com.en_workshop.webcrawlerakka.dao.impl.RestDomainDao;
import com.en_workshop.webcrawlerakka.dao.impl.RestLinkDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;
import org.drools.KnowledgeBase;
import org.drools.common.DroolsObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Radu Ciumag
 */
public class WebCrawler {
    private static final Logger LOG = LoggerFactory.getLogger(WebCrawler.class);

    public static String DOMAIN_NAME_FILTER;
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

//        DroolsObjectInputStream x = new DroolsObjectInputStream(new FileInputStream(""));
//        x.re
//        DroolsObjectInputStream objectInStream = new DroolsObjectInputStream(new ByteArrayInputStream(("rule \"Good Bye\"\n" +
//                "      dialect \"java\"\n" +
//                "  when\n" +
//                "      Message( status == Message.GOODBYE, message : message )\n" +
//                "  then\n" +
//                "      System.out.println( message ); \n" +
//                "end").getBytes("UTF-8")));
//        KnowledgeBase knowledgeBase = (KnowledgeBase) objectInStream.readObject();

        LOG.info("Web crawler starting ...");

        LOG.debug("Add sample (test) data ...");
//        Domain wikiDomain = DomainDao.add(new Domain("ro.wikipedia.org", 20000, 0));
        Domain domain = new Domain("www.archeus.ro", 1, 0);


        DOMAIN_NAME_FILTER = "www.archeus.ro"; // to prevent crawling all domains!


        PersistenceActor.getDomainDao().add(domain);
        Thread.sleep(1000); // allow the domain to get to DB...
//        if (null != wikiDomain) {
//            LinkDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Pagina_principal%C4%83");
//        }

        LinkDao linkDao = PersistenceActor.getLinkDao();
        linkDao.create(new Link(domain.getName(), null, "http://www.archeus.ro/lingvistica/CautareDex?query=bun&lang=ro"));
        linkDao.create(new Link(domain.getName(), null, "http://www.archeus.ro/lingvistica/CautareDex?query=CARE"));
        linkDao.create(new Link(domain.getName(), null, "http://www.archeus.ro/lingvistica/CautareDex?query=VALOARE"));
        linkDao.create(new Link(domain.getName(), null, "http://www.archeus.ro/lingvistica/CautareDex?query=mare&lang=ro"));
        linkDao.create(new Link(domain.getName(), null, "http://www.archeus.ro/lingvistica/CautareWebster?query=GREAT"));
        linkDao.create(new Link(domain.getName(), null, "http://www.archeus.ro/lingvistica/CautareWebster?query=DISTANCE"));
        if (linkDao instanceof RestLinkDao) {
            ((RestLinkDao)linkDao).flush();
        }


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

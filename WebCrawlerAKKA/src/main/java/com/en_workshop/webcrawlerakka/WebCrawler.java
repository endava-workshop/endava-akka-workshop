package com.en_workshop.webcrawlerakka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.en_workshop.webcrawlerakka.akka.actors.MasterActor;
import com.en_workshop.webcrawlerakka.akka.requests.StartMasterRequest;
import com.en_workshop.webcrawlerakka.dao.DomainDao;
import com.en_workshop.webcrawlerakka.dao.LinkDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Radu Ciumag
 */
public class WebCrawler {
    private static final Logger LOG = Logger.getLogger(WebCrawler.class);

    public static void main(String[] args) {
        LOG.info("Web crawler starting ...");

        LOG.debug("Add sample (test) data ...");
        Domain wikiDomain = DomainDao.add(new Domain("ro.wikipedia.org", 20000, 0));
        if (null != wikiDomain) {
            LinkDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Pagina_principal%C4%83");
        }

//        Domain debianDomain = WebDomainDao.add(new Domain("wiki.debian.org", 30000, 0));
//        if (null != debianDomain) {
//            WebUrlDao.add(debianDomain, "http://" + debianDomain.getName());
//        }
        LOG.debug("Add sample (test) data: DONE");

        LOG.debug("Actor system initializing ...");
        ActorSystem actorSystem = ActorSystem.create(WebCrawlerConstants.SYSTEM_NAME);

        ActorRef masterActor = actorSystem.actorOf(Props.create(MasterActor.class), WebCrawlerConstants.MASTER_ACTOR_NAME);
        masterActor.tell(new StartMasterRequest(), ActorRef.noSender());
        LOG.debug("Actor system initialize: DONE");

        microConsole();
    }

    private static void microConsole() {
        try {
            System.out.print("crawler> ");

            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            while (!"exit".equals(input)) {
                /* Process the inputted command */


                System.out.print("crawler> ");
                input = reader.readLine();
            }
        } catch (IOException exc) {
            LOG.error(exc.getMessage(), exc);
        }
    }
}

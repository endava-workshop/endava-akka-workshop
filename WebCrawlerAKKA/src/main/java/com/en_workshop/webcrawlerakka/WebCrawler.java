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

        LOG.debug("Add sample (test) data ...");
        WebDomain wikiDomain = WebDomainDao.add(new WebDomain("http://ro.wikipedia.org/", "Wikipedia", 20000, 0));
        if (null != wikiDomain) {
            WebUrlDao.add(wikiDomain, wikiDomain.getBaseUrl());

            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Pagina_principal%C4%83");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Pagina_principal%C4%83");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Portal:R%C4%83sfoire");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Wikipedia:Cafenea");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Special:Aleatoriu");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Special:Schimb%C4%83ri_recente");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Wikipedia:Proiectul_s%C4%83pt%C4%83m%C3%A2nii");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Ajutor:Cuprins");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Portal:Comunitate");
            WebUrlDao.add(wikiDomain, "http://donate.wikimedia.org/wiki/Special:FundraiserRedirector?utm_source=donate&amp;utm_medium=sidebar&amp;utm_campaign=C13_ro.wikipedia.org&amp;uselang=ro");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/w/index.php?title=Special:Carte&amp;bookcmd=book_creator&amp;referer=Pagina+principal%C4%83");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/w/index.php?title=Special:Carte&amp;bookcmd=render_article&amp;arttitle=Pagina+principal%C4%83&amp;oldid=7499118&amp;writer=rl");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/w/index.php?title=Pagina_principal%C4%83&amp;printable=yes");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Special:Ce_se_leag%C4%83_aici/Pagina_principal%C4%83");
            WebUrlDao.add(wikiDomain, "http://ro.wikipedia.org/wiki/Special:Modific%C4%83ri_corelate/Pagina_principal%C4%83");
        }

        WebDomain debianDomain = WebDomainDao.add(new WebDomain("https://wiki.debian.org/", "WikiDebian", 30000, 0));
        if (null != debianDomain) {
            WebUrlDao.add(debianDomain, debianDomain.getBaseUrl());
        }
        LOG.debug("Add sample (test) data: DONE");

        LOG.debug("Actor system initializing ...");
        ActorSystem actorSystem = ActorSystem.create(WebCrawlerConstants.SYSTEM_NAME);

        ActorRef masterActor = actorSystem.actorOf(Props.create(MasterActor.class), WebCrawlerConstants.MASTER_ACTOR_NAME);
        masterActor.tell(new StartMasterRequest(), ActorRef.noSender());
        LOG.debug("Actor system initialize: DONE");
    }
}

package com.en_workshop.webcrawlerakka.dao.impl;

import com.en_workshop.webcrawlerakka.dao.LinkDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.DomainLink;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.rest.*;
import scala.Option;
import scala.Some;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ionut on 20.04.2014.
 */
public class RestLinkDao implements LinkDao {

    private static final int BUFF_SIZE = 1000;
    private int pageSize = 2000;

    private Map<String, Queue<Link>> domainLinks = new HashMap<>(); // per domainName
    private Map<String, Set<DomainLink_>> buff = new HashMap<>(); // per domainName

    @Override
    public Link getNextForCrawling(Domain domain) {
        // check pre-fetch area
        String domainName = domain.getName();
        Queue<Link> links;
        synchronized (domainLinks) {
            links = domainLinks.get(domainName);
            if (links == null) {
                links = new ArrayBlockingQueue(pageSize);
                domainLinks.put(domainName, links);
            }
        }
        synchronized (links) {
            Link result = links.poll();
            if (result == null) {
                flush(domainName);
                List<Link> not_visited = SimpleURLClient.getURLs(domainName, "NOT_VISITED", 0, pageSize);
                links.addAll(not_visited);
                result = links.poll();
            }
            return result;
        }
    }

    @Override
    public void update(Link link) {
        SimpleURLClient.setURLStatus(link.getUrl(), link.getStatus().toString());
    }

    @Override
    public void create(DomainLink domainLink) {
        Domain domain = domainLink.getDomain();
        Link link = domainLink.getLink();

        if (link.getStatus() != null) {
            switch (link.getStatus()) {
                case NOT_VISITED:
                    String domainName = link.getDomain();
                    DomainUrl_ domainUrl_ = DomainURLClient.buildDomainUrl(domainName, domainName, domain.getCoolDownPeriod());
                    SimpleUrl_ dto = new SimpleUrl_(link.getUrl(), Option.apply(link.getSourceDomain()), Option.apply(domainName), Option.apply(link.getUrl()), "NOT_VISITED");
                    DomainLink_ domainLink_ = new DomainLink_(domainUrl_, dto);

                    Set<DomainLink_> urls;
                    synchronized (buff) { // create domain entry if it does not exist
                        urls = buff.get(domainName);
                        if (urls == null) {
                            urls = new HashSet<>(BUFF_SIZE);
                            buff.put(domainName, urls);
                        }
                    }
                    HashSet<DomainLink_> toSave = null;
                    synchronized (urls) { // accumulator
                        urls.add(domainLink_);
                        if (urls.size() >= BUFF_SIZE) {
                            toSave = new HashSet<>(urls); // prepare for flush
                            urls.clear();
                            buff.put(domainName, urls);
                        }
                    }
                    if (toSave != null) {
                        SimpleURLClient.addDomainLinks(toSave, false);
                    }
                    break;
                case VISITED:
                    SimpleURLClient.setURLStatus(link.getUrl(), link.getStatus().toString());
                    break;
                case FAILED:
                    SimpleURLClient.setURLStatus(link.getUrl(), link.getStatus().toString());
                    break;
            }
        } else {
            System.out.println("\n\n NULL STATUS ");
        }

    }

    public void flush() { // TODO remove this
        synchronized (buff) {
            for (Set<DomainLink_> urls : buff.values()) {
                if (urls.size() > 0) {
                    SimpleURLClient.addDomainLinks(urls, true);
                }
            }
            buff.clear();
        }
    }
    public void flush(String domainName) { // TODO remove this
        synchronized (buff) {
            Set<DomainLink_> urls = buff.get(domainName);
            if (urls != null && urls.size() > 0) {
                SimpleURLClient.addDomainLinks(urls, true);
            }
            urls = new HashSet<>(BUFF_SIZE);
            buff.put(domainName, urls);
        }
    }
}

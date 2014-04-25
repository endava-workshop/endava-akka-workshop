package com.en_workshop.webcrawlerakka.dao.impl;

import com.en_workshop.webcrawlerakka.dao.LinkDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.rest.SimpleURLClient;
import com.en_workshop.webcrawlerakka.rest.SimpleUrl_;
import scala.Option;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ionut on 20.04.2014.
 */
public class RestLinkDao implements LinkDao {

    private static final int BUFF_SIZE = 500;
    private int pageSize = 1000;

    private ConcurrentMap<String, Queue<Link>> domainLinks = new ConcurrentHashMap<>();
    private Object LOCK_BUFF = new Object();
    private Set<SimpleUrl_> buff = new HashSet<>(BUFF_SIZE);

    @Override
    public Link getNextForCrawling(Domain domain) {
        String domainName = domain.getName();
        Queue<Link> links = new ArrayBlockingQueue(pageSize);
        synchronized (domainLinks) {
            Queue<Link> oldLinks = domainLinks.putIfAbsent(domainName, links);
            if (oldLinks != null) {
                links = oldLinks;
            }
        }
        synchronized (links) {
            Link result = links.poll();
            if (result == null) {
                flush();
                List<Link> not_visited = SimpleURLClient.getURLs(domainName, "NOT_VISITED", 0, pageSize);
                links.addAll(not_visited);
                result = links.poll();
            }
            return result;
        }
    }

    @Override
    public void create(Link link) {
        if (link.getStatus() != null) {
            switch (link.getStatus()) {
                case NOT_VISITED:
                    SimpleUrl_ dto = new SimpleUrl_(link.getUrl(), Option.apply(link.getSourceDomain()), Option.apply(link.getDomain()), Option.apply(link.getUrl()), "NOT_VISITED");
                    Set<SimpleUrl_> toSave = null;
                    synchronized (LOCK_BUFF) {
                        buff.add(dto);
                        if (buff.size() >= BUFF_SIZE) {
                            toSave = buff;
                            buff = new HashSet<>(BUFF_SIZE);
                        }
                    }
                    if (toSave != null) {
                        SimpleURLClient.addURLs(toSave, false);
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
        synchronized (LOCK_BUFF) {
            if (buff.size() > 0) {
                SimpleURLClient.addURLs(buff, true);
                buff = new HashSet<>(BUFF_SIZE);
            }
        }
    }
}

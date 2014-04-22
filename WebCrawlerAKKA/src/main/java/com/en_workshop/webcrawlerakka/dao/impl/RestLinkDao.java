package com.en_workshop.webcrawlerakka.dao.impl;

import com.en_workshop.webcrawlerakka.dao.LinkDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.rest.SimpleURLClient;
import com.en_workshop.webcrawlerakka.rest.SimpleUrl_;
import scala.Option;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ionut on 20.04.2014.
 */
public class RestLinkDao implements LinkDao {

    private static final int BUFF_SIZE = 500;
    private int pageSize = 1000;

    private Queue<Link> links = new ArrayBlockingQueue(pageSize);
    private Object LOCK_BUFF = new Object();
    private Set<SimpleUrl_> buff = new HashSet<>(BUFF_SIZE);

    @Override
    public Link getNextForCrawling(Domain domain) {
        synchronized (links) {
            Link result = links.poll();
            if (result == null) {
                List<Link> not_visited = SimpleURLClient.getURLs(domain.getName(), "NOT_VISITED", 0, pageSize);
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
                        SimpleURLClient.addURLs(toSave);
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
            SimpleURLClient.addURLs(buff);
            buff = new HashSet<>(BUFF_SIZE);
        }
    }
}

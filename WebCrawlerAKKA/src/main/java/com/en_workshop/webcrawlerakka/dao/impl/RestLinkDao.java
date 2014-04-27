package com.en_workshop.webcrawlerakka.dao.impl;

import com.en_workshop.webcrawlerakka.dao.LinkDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.DomainLink;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.enums.LinkStatus;
import com.en_workshop.webcrawlerakka.rest.*;
import scala.Option;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ionut on 20.04.2014.
 */
public class RestLinkDao implements LinkDao {

    private static final int BUFF_SIZE_SAVE = 1000;
    private static final int BUFF_SIZE_UPDATE = 100;
    private int pageSize = 2000;

    private Map<String, Queue<Link>> domainLinks = new HashMap<>(); // per domainName
    private List<DomainLink_> buffToSave = new ArrayList<>(BUFF_SIZE_SAVE);
    private Object BUFF_TO_SAVE_LOCK = new Object();

    private List<String> buffVisited = new ArrayList<>(BUFF_SIZE_UPDATE);
    private Object BUFF_VISITED_LOCK = new Object();

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
                flush();
                List<Link> not_visited = SimpleURLClient.getURLs(domainName, "NOT_VISITED", 0, pageSize);
                links.addAll(not_visited);
                result = links.poll();
            }
            return result;
        }
    }

    @Override
    public void update(Link link) {
        switch (link.getStatus()) {
            case NOT_VISITED:
//                SimpleURLClient.setURLStatus(link.getUrl(), link.getStatus().toString());
                break;
            case VISITED:
                synchronized (BUFF_VISITED_LOCK) {
                    buffVisited.add(link.getUrl());
                    List<String> toUpdate = null;
                    if (buffVisited.size() >= BUFF_SIZE_UPDATE) {
                        toUpdate = buffVisited;
                        buffVisited = new ArrayList<>(BUFF_SIZE_UPDATE);
                    }
                    if (toUpdate != null) {
                        SimpleURLClient.setURLsStatus(toUpdate, LinkStatus.VISITED.toString());
                    }
                }
                break;
            case FAILED:
                SimpleURLClient.markURLError(link.getUrl());
                break;
            default:
                break;
        }
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

                    List<DomainLink_> toSave = null;
                    synchronized (BUFF_TO_SAVE_LOCK) { // create domain entry if it does not exist
                        buffToSave.add(domainLink_);
                        if (buffToSave.size() >= BUFF_SIZE_SAVE) {
                            toSave = buffToSave;
                            buffToSave = new ArrayList<>(BUFF_SIZE_SAVE);
                        }
                    }
                    if (toSave != null) {
                        SimpleURLClient.addDomainLinks(toSave, false);
                    }
                    break;
                case VISITED:
                   update(link);
                    break;
                case FAILED:
                    update(link);
                    break;
            }
        } else {
            System.out.println("\n\n NULL STATUS ");
        }

    }

    public void flush() {
        List<DomainLink_> toSave = null;
        synchronized (BUFF_TO_SAVE_LOCK) {
            if (buffToSave.size() > 0) {
                toSave = buffToSave;
                buffToSave = new ArrayList<>();
            }
        }
        if (toSave != null) {
            SimpleURLClient.addDomainLinks(toSave, true);

        }
        List<String> toUpdate = null;
        synchronized (BUFF_VISITED_LOCK) {
            if (buffVisited.size() > 0) {
                toUpdate = buffVisited;
                buffVisited = new ArrayList<>();
            }
        }
        if (toUpdate != null) {
            SimpleURLClient.setURLsStatus(toUpdate, LinkStatus.VISITED.toString());
        }
    }

}

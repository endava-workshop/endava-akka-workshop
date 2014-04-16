package com.en_workshop.webcrawlerakka.entities;

import com.en_workshop.webcrawlerakka.enums.LinkStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Radu Ciumag
 */
public class Link {

    public static final List<Link> LINKS = Collections.synchronizedList(new ArrayList<Link>());

    private final String domain;

    private final String sourceDomain; //null daca domeniul pe care a fost gasit este acelasi cu domain

    private final String url;
    private final LinkStatus status;

    public Link(final String domain, String sourceDomain, final String url) {
        this(domain, sourceDomain, url, LinkStatus.NOT_VISITED);
    }

    public Link(final String domain, String sourceDomain, final String url, final LinkStatus status) {
        this.domain = domain;
        this.sourceDomain = domain.equals(sourceDomain) ? null : sourceDomain;
        this.url = url;
        this.status = status;
    }

    public String getDomain() {
        return domain;
    }

    public String getUrl() {
        return url;
    }

    public LinkStatus getStatus() {
        return status;
    }

    public String getSourceDomain() {
        return sourceDomain;
    }

    @Override
    public String toString() {
        return "Link{" +
                "domain=" + domain +
                ", sourceDomain=" + sourceDomain +
                ", url='" + url + '\'' +
                ", status=" + status +
                '}';
    }

}

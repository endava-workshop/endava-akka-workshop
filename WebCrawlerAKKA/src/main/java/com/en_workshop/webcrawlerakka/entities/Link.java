package com.en_workshop.webcrawlerakka.entities;

import com.en_workshop.webcrawlerakka.enums.LinkStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Radu Ciumag
 */
public class Link {

    public static final List<Link> LINKS = new ArrayList<>();

    private final String domain;
    private final String url;
    private final LinkStatus status;

    public Link(final String domain, final String url) {
        this(domain, url, LinkStatus.NOT_VISITED);
    }

    public Link(final String domain, final String url, final LinkStatus status) {
        this.domain = domain;
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

    @Override
    public String toString() {
        return "Link{" +
                "domain=" + domain +
                ", url='" + url + '\'' +
                ", status=" + status +
                '}';
    }

}

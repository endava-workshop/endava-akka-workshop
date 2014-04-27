package com.en_workshop.webcrawlerakka.entities;

import com.en_workshop.webcrawlerakka.enums.LinkStatus;
import scala.util.parsing.combinator.testing.Str;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Radu Ciumag
 */
public class Link {

    private final String domain;
    private final String url;
    private final LinkStatus status;

    private final String sourceDomain; //null daca domeniul pe care a fost gasit este acelasi cu domain
    private final String sourceLink;


    public Link(final String domain, String sourceDomain, final String url, final String sourceLink) {
        this(domain, sourceDomain, url, sourceLink, LinkStatus.NOT_VISITED);
    }

    public Link(final String domain, String sourceDomain, final String url, final String sourceLink, final LinkStatus status) {
        this.domain = domain;
        this.sourceDomain = domain.equals(sourceDomain) ? null : sourceDomain;
        this.sourceLink = sourceLink;
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

    public String getSourceLink() {
        return sourceLink;
    }

    @Override
    public String toString() {
        return "Link{" +
                "domain='" + domain + '\'' +
                ", url='" + url + '\'' +
                ", status=" + status +
                ", sourceDomain='" + sourceDomain + '\'' +
                ", sourceLink='" + sourceLink + '\'' +
                '}';
    }

}

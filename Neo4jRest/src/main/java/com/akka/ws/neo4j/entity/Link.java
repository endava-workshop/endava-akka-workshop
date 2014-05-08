package com.akka.ws.neo4j.entity;

import com.akka.ws.neo4j.enums.LinkStatus;

/**
 * @author Radu Ciumag
 */
public class Link {

    private final String domain;
    private final String url;
    private final LinkStatus status;

    private final String sourceLink;


    public Link(final String domain, final String url, final String sourceLink) {
        this(domain, url, sourceLink, LinkStatus.NOT_VISITED);
    }

    public Link(final String domain, final String url, LinkStatus status) {
        this(domain, url, domain, status);
    }
    
    public Link(final String domain, final String url, final String sourceLink, final LinkStatus status) {
        this.domain = domain;
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

    public String getSourceLink() {
        return sourceLink;
    }

    @Override
    public String toString() {
        return "Link{" +
                "domain='" + domain + '\'' +
                ", url='" + url + '\'' +
                ", status=" + status +
                ", sourceLink='" + sourceLink + '\'' +
                '}';
    }

}

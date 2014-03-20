package com.en_workshop.webcrawlerakka.entities;

import com.en_workshop.webcrawlerakka.enums.LinkStatus;

/**
 * @author Radu Ciumag
 */
public class LinkCloner {

    private String domain;
    private String url;
    private LinkStatus status;

    public LinkCloner(final Link link) {
        this.domain = link.getDomain();
        this.url = link.getUrl();
        this.status = link.getStatus();
    }

    public LinkCloner withStatus(final LinkStatus status) {
        this.status = status;

        return this;
    }

    public Link build() {
        return new Link(domain, url, status);
    }
}

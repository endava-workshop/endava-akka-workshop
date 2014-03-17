package com.en_workshop.webcrawlerakka.entities;

import com.en_workshop.webcrawlerakka.enums.WebUrlStatus;

/**
 * @author Radu Ciumag
 */
public class WebUrlCloner {

    private String domain;
    private String url;
    private WebUrlStatus status;

    public WebUrlCloner(final Link link) {
        this.domain = link.getDomain();
        this.url = link.getUrl();
        this.status = link.getStatus();
    }

    public WebUrlCloner withStatus(final WebUrlStatus status) {
        this.status = status;

        return this;
    }

    public Link build() {
        return new Link(domain, url, status);
    }
}

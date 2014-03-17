package com.en_workshop.webcrawlerakka.entities;

import com.en_workshop.webcrawlerakka.enums.WebUrlStatus;

/**
 * @author Radu Ciumag
 */
public class WebUrlCloner {

    private Domain domain;
    private String url;
    private WebUrlStatus status;

    public WebUrlCloner(final WebUrl webUrl) {
        this.domain = webUrl.getDomain();
        this.url = webUrl.getUrl();
        this.status = webUrl.getStatus();
    }

    public WebUrlCloner withStatus(final WebUrlStatus status) {
        this.status = status;

        return this;
    }

    public WebUrl build() {
        return new WebUrl(domain, url, status);
    }
}

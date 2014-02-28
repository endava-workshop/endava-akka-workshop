package com.en_workshop.webcrawlerakka.entities;

import com.en_workshop.webcrawlerakka.enums.WebUrlStatus;

/**
 * @author Radu Ciumag
 */
public class WebUrlCloner {

    private WebDomain webDomain;
    private String url;
    private WebUrlStatus status;

    public WebUrlCloner(final WebUrl webUrl) {
        this.webDomain = webUrl.getWebDomain();
        this.url = webUrl.getUrl();
        this.status = webUrl.getStatus();
    }

    public WebUrlCloner withStatus(final WebUrlStatus status) {
        this.status = status;

        return this;
    }

    public WebUrl build() {
        return new WebUrl(webDomain, url, status);
    }
}

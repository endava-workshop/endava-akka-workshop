package com.en_workshop.webcrawlerakka.entities;

/**
 * @author Radu Ciumag
 */
public class WebDomainCloner {

    private String baseUrl;
    private String name;
    private long cooldownPeriod;
    private long crawledAt;

    public WebDomainCloner(final WebDomain webDomain) {
        this.baseUrl = webDomain.getBaseUrl();
        this.name = webDomain.getName();
        this.cooldownPeriod = webDomain.getCooldownPeriod();
        this.crawledAt = webDomain.getCrawledAt();
    }

    public WebDomainCloner withCrawledAt(final long crawledAt) {
        this.crawledAt = crawledAt;

        return this;
    }

    public WebDomain build() {
        return new WebDomain(baseUrl, name, cooldownPeriod, crawledAt);
    }
}

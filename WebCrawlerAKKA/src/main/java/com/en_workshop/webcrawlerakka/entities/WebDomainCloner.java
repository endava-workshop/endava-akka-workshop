package com.en_workshop.webcrawlerakka.entities;

/**
 * @author Radu Ciumag
 */
public class WebDomainCloner {

    private String baseUrl;
    private String name;
    private long cooldownPeriod;
    private long crawledTime;

    public WebDomainCloner(final WebDomain webDomain) {
        this.baseUrl = webDomain.getBaseUrl();
        this.name = webDomain.getName();
        this.cooldownPeriod = webDomain.getCooldownPeriod();
        this.crawledTime = webDomain.getCrawledTime();
    }

    public WebDomainCloner withCrawledTime(final long crawledTime) {
        this.crawledTime = crawledTime;

        return this;
    }

    public WebDomain build() {
        return new WebDomain(baseUrl, name, cooldownPeriod, crawledTime);
    }
}

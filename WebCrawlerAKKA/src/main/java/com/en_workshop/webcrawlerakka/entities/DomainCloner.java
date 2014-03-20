package com.en_workshop.webcrawlerakka.entities;

/**
 * @author Radu Ciumag
 */
public class DomainCloner {

    private String name;
    private long cooldownPeriod;
    private long crawledAt;

    public DomainCloner(final Domain domain) {
        this.name = domain.getName();
        this.cooldownPeriod = domain.getCoolDownPeriod();
        this.crawledAt = domain.getCrawledAt();
    }

    public DomainCloner withCrawledAt(final long crawledAt) {
        this.crawledAt = crawledAt;

        return this;
    }

    public Domain build() {
        return new Domain(name, cooldownPeriod, crawledAt);
    }
}

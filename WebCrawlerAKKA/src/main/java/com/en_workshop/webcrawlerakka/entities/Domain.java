package com.en_workshop.webcrawlerakka.entities;

import com.en_workshop.webcrawlerakka.enums.DomainStatus;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Radu Ciumag
 */
public class Domain {

    private final String name;
    private final long coolDownPeriod;
    private final long crawledAt;
    private final DomainStatus domainStatus;

    public Domain(final String name, final long coolDownPeriod, final long crawledAt, final DomainStatus domainStatus) {
        this.name = name;
        this.coolDownPeriod = coolDownPeriod;
        this.crawledAt = crawledAt;
        this.domainStatus = domainStatus;
    }

    public Domain(final String name, final long coolDownPeriod, final long crawledAt) {
        this(name, coolDownPeriod, crawledAt, DomainStatus.FOUND);
    }

    public String getName() {
        return name;
    }

    public long getCrawledAt() {
        return crawledAt;
    }

    public long getCoolDownPeriod() {
        return coolDownPeriod;
    }

    public DomainStatus getDomainStatus() {
        return domainStatus;
    }

    @Override
    public String toString() {
        return "Domain{" +
                "name='" + name + '\'' +
                ", coolDownPeriod=" + coolDownPeriod +
                ", crawledAt=" + crawledAt +
                ", domainStatus=" + domainStatus +
                '}';
    }
}

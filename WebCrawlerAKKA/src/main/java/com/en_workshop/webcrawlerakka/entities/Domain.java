package com.en_workshop.webcrawlerakka.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Radu Ciumag
 */
public class Domain {

    public static final List<Domain> DOMAINS = new ArrayList<>();

    private final String name;
    private final long coolDownPeriod;
    private final long crawledAt;

    public Domain(final String name, final long coolDownPeriod, final long crawledAt) {
        this.name = name;
        this.coolDownPeriod = coolDownPeriod;
        this.crawledAt = crawledAt;
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

    @Override
    public String toString() {
        return "Domain{" +
                ", name='" + name + '\'' +
                ", coolDownPeriod=" + coolDownPeriod +
                ", crawledAt=" + crawledAt +
                '}';
    }

}

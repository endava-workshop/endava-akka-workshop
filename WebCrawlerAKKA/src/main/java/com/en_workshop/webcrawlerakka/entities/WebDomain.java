package com.en_workshop.webcrawlerakka.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Radu Ciumag
 */
public class WebDomain {

    public static List<WebDomain> DOMAINS = new ArrayList<WebDomain>();

    private final String baseUrl;
    private final String name;
    private final long cooldownPeriod;
    private final long crawledTime;

    public WebDomain(final String baseUrl, final String name, final long cooldownPeriod, final long crawledTime) {
        this.baseUrl = baseUrl;
        this.name = name;
        this.cooldownPeriod = cooldownPeriod;
        this.crawledTime = crawledTime;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getName() {
        return name;
    }

    public long getCrawledTime() {
        return crawledTime;
    }

    public long getCooldownPeriod() {
        return cooldownPeriod;
    }

    @Override
    public String toString() {
        return "WebDomain{" +
                "baseUrl='" + baseUrl + '\'' +
                ", name='" + name + '\'' +
                ", cooldownPeriod=" + cooldownPeriod +
                ", crawledTime=" + crawledTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WebDomain webDomain = (WebDomain) o;
        if (baseUrl != null ? !baseUrl.equals(webDomain.baseUrl) : webDomain.baseUrl != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return baseUrl != null ? baseUrl.hashCode() : 0;
    }
}

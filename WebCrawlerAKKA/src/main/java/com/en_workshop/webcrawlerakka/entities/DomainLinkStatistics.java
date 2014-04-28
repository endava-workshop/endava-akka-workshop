package com.en_workshop.webcrawlerakka.entities;

import com.en_workshop.webcrawlerakka.enums.DomainStatus;

/**
 * Statistics for the links of a domain.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/10/14
 */
public class DomainLinkStatistics {

    private String domain;
    private DomainStatus domainStatus;

    private int identified;
    private int downloaded;
    private int failed;

    public DomainLinkStatistics(String domain) {
        this.domain = domain;
    }

    public void addIdentifiedLinks() {
        identified++;
    }

    public void addDownloadedLinks() {
        downloaded++;
    }

    public void addFailedLinks() {
        failed++;
    }

    public int getIdentified() {
        return identified;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public int getFailed() {
        return failed;
    }

    public String getDomain() {
        return domain;
    }

    public DomainStatus getDomainStatus() {
        return domainStatus;
    }

    public void setDomainStatus(DomainStatus domainStatus) {
        this.domainStatus = domainStatus;
    }

    @Override
    public String toString() {
        return "DomainLinkStatistics{" +
                "domain='" + domain + '\'' +
                ", domainStatus=" + domainStatus +
                ", identified=" + identified +
                ", downloaded=" + downloaded +
                ", failed=" + failed +
                '}';
    }
}

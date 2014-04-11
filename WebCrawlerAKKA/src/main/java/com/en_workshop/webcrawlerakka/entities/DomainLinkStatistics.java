package com.en_workshop.webcrawlerakka.entities;

/**
 * Statistics for the links of a domain.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/10/14
 */
public class DomainLinkStatistics {

    private String domain;

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

    @Override
    public String toString() {
        return "DomainLinkStatistics{" +
                "domain='" + domain + '\'' +
                ", identified=" + identified +
                ", downloaded=" + downloaded +
                ", failed=" + failed +
                '}';
    }
}

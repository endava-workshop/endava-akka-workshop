package entity;

/**
 * Created by ionut on 26.04.2014.
 */
public class DomainLink {
    private DomainURL domainURL;
    private SimpleURL simpleURL;

    public DomainLink(DomainURL domainURL, SimpleURL simpleURL) {
        this.domainURL = domainURL;
        this.simpleURL = simpleURL;
    }

    public DomainURL getDomainURL() {
        return domainURL;
    }

    public SimpleURL getSimpleURL() {
        return simpleURL;
    }
}

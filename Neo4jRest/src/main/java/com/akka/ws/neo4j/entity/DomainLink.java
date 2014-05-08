package com.akka.ws.neo4j.entity;

/**
 *
 * Created by roxana on 26.04.2014.
 */
public class DomainLink {

    private Domain domain;
    private Link link;

    public DomainLink(Domain domain, Link link) {
        this.domain = domain;
        this.link = link;
    }

    public Domain getDomain() {
        return domain;
    }

    public Link getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "DomainLink{" +
                "domain=" + domain +
                ", link=" + link +
                '}';
    }
}

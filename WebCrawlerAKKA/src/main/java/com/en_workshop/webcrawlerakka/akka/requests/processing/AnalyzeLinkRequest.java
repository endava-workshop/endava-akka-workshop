package com.en_workshop.webcrawlerakka.akka.requests.processing;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;

/**
 * Request to analyze the links.
 *
 * Created by roxana on 3/13/14.
 */
public class AnalyzeLinkRequest extends MessageRequest implements ProcessingRequest {

    private final String sourceLink;
    private final Domain sourceDomain;
    private final String link;

    public AnalyzeLinkRequest(final Domain sourceDomain, final String sourceLink, final String link) {
        super(System.currentTimeMillis());

        this.sourceDomain = sourceDomain;
        this.sourceLink = sourceLink;
        this.link = link;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public Domain getSourceDomain() {
        return sourceDomain;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "AnalyzeLinkRequest{" +
                "sourceLink='" + sourceLink + '\'' +
                ", sourceDomain='" + sourceDomain + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}

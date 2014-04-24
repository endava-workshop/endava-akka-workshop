package com.en_workshop.webcrawlerakka.akka.requests.processing;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;

/**
 * Request to analyze the links.
 *
 * Created by roxana on 3/13/14.
 */
public class AnalyzeLinkRequest extends MessageRequest {

    private final String sourceLink;
    private final String sourceDomainName;
    private final String link;

    public AnalyzeLinkRequest(final String sourceDomainName, final String sourceLink, final String link) {
        super(System.currentTimeMillis());

        this.sourceDomainName = sourceDomainName;
        this.sourceLink = sourceLink;
        this.link = link;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public String getSourceDomainName() {
        return sourceDomainName;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "AnalyzeLinkRequest{" +
                "sourceLink='" + sourceLink + '\'' +
                ", sourceDomainName='" + sourceDomainName + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}

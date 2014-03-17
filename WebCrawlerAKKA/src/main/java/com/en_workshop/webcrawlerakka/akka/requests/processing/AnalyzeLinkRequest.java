package com.en_workshop.webcrawlerakka.akka.requests.processing;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import org.apache.log4j.Logger;

/**
 * Request to analyze the links.
 *
 * Created by roxana on 3/13/14.
 */
public class AnalyzeLinkRequest extends MessageRequest {

    private static final Logger LOG = Logger.getLogger(AnalyzeLinkRequest.class);

    private final Domain sourceDomain;
    private final String link;

    public AnalyzeLinkRequest(final Domain sourceDomain, final String link) {
        super(System.currentTimeMillis());

        this.sourceDomain = sourceDomain;
        this.link = link;
    }

    public Domain getSourceDomain() {
        return sourceDomain;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "AnalyzeLinkRequest{webDomain=" + sourceDomain + ", link=" + link + "}";
    }
}

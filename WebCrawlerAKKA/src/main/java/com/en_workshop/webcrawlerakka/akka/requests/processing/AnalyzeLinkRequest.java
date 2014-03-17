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

    private final String sourceDomainName;
    private final String link;

    public AnalyzeLinkRequest(final String sourceDomainName, final String link) {
        super(System.currentTimeMillis());

        this.sourceDomainName = sourceDomainName;
        this.link = link;
    }

    public String getSourceDomainName() {
        return sourceDomainName;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "AnalyzeLinkRequest{webDomainName=" + sourceDomainName + ", link=" + link + "}";
    }
}

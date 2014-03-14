package com.en_workshop.webcrawlerakka.akka.requests.processing;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.WebDomain;
import com.en_workshop.webcrawlerakka.entities.WebUrl;
import org.apache.log4j.Logger;

/**
 * Request to analyze the links.
 *
 * Created by roxana on 3/13/14.
 */
public class AnalyzeLinkRequest extends MessageRequest {

    private static final Logger LOG = Logger.getLogger(AnalyzeLinkRequest.class);

    private final WebDomain sourceDomain;
    private final String link;

    public AnalyzeLinkRequest(final WebDomain sourceDomain, final String link) {
        super(System.currentTimeMillis());

        this.sourceDomain = sourceDomain;
        this.link = link;
    }

    public WebDomain getSourceDomain() {
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

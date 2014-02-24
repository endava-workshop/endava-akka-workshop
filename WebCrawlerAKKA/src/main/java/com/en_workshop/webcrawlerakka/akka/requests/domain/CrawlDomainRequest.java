package com.en_workshop.webcrawlerakka.akka.requests.domain;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.WebDomain;
import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class CrawlDomainRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(CrawlDomainRequest.class);

    private final WebDomain webDomain;

    public CrawlDomainRequest(final WebDomain webDomain) {
        super(System.currentTimeMillis());

        this.webDomain = webDomain;
    }

    public WebDomain getWebDomain() {
        return webDomain;
    }
}

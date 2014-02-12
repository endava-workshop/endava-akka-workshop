package com.en_workshop.webcrawlerakka.akka.requests;

import com.en_workshop.webcrawlerakka.entities.WebDomain;
import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class CrawleDomainRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(CrawleDomainRequest.class);

    private final WebDomain webDomain;

    public CrawleDomainRequest(final WebDomain webDomain) {
        super(System.currentTimeMillis());

        this.webDomain = webDomain;
    }

    public WebDomain getWebDomain() {
        return webDomain;
    }
}

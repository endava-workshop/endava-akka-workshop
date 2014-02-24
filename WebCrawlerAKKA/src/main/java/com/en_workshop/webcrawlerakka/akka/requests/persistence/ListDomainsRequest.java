package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import org.apache.log4j.Logger;

/**
 * Request the list of domains to crawl
 *
 * @author Radu Ciumag
 */
public class ListDomainsRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(ListDomainsRequest.class);

    /**
     * The default constructor
     */
    public ListDomainsRequest() {
        super(System.currentTimeMillis());
    }
}

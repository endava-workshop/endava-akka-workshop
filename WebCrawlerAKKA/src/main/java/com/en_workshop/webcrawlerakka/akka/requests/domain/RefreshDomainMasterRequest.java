package com.en_workshop.webcrawlerakka.akka.requests.domain;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class RefreshDomainMasterRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(RefreshDomainMasterRequest.class);

    public RefreshDomainMasterRequest() {
        super(System.currentTimeMillis());
    }
}

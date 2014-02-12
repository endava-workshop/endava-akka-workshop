package com.en_workshop.webcrawlerakka.akka.requests;

import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class StartDomainMasterRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(StartDomainMasterRequest.class);

    public StartDomainMasterRequest() {
        super(System.currentTimeMillis());
    }
}

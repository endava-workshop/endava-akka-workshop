package com.en_workshop.webcrawlerakka.akka.requests;

import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class StopDomainMasterRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(StopDomainMasterRequest.class);

    public StopDomainMasterRequest() {
        super(System.currentTimeMillis());
    }
}

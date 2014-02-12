package com.en_workshop.webcrawlerakka.akka.requests;

import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class StopProcessingMasterRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(StopProcessingMasterRequest.class);

    public StopProcessingMasterRequest() {
        super(System.currentTimeMillis());
    }
}

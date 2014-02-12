package com.en_workshop.webcrawlerakka.akka.requests;

import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class StopMasterRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(StopMasterRequest.class);

    public StopMasterRequest() {
        super(System.currentTimeMillis());
    }
}

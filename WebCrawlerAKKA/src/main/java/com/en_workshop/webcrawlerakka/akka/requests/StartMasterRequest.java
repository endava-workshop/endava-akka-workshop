package com.en_workshop.webcrawlerakka.akka.requests;

import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class StartMasterRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(StartMasterRequest.class);

    public StartMasterRequest() {
        super(System.currentTimeMillis());
    }
}

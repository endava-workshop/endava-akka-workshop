package com.en_workshop.webcrawlerakka.akka.requests;

import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class StartProcessingMasterRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(StartProcessingMasterRequest.class);

    public StartProcessingMasterRequest() {
        super(System.currentTimeMillis());
    }
}

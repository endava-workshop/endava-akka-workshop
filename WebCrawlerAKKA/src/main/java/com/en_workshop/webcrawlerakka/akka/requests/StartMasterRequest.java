package com.en_workshop.webcrawlerakka.akka.requests;

/**
 * @author Radu Ciumag
 */
public class StartMasterRequest extends MessageRequest {

    public StartMasterRequest() {
        super(System.currentTimeMillis());
    }
}

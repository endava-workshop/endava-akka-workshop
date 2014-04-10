package com.en_workshop.webcrawlerakka.akka.requests.other.control;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;

/**
 * Control actors request
 *
 * @author Radu Ciumag
 */
public class ControlRequest extends MessageRequest {

    public ControlRequest() {
        super(System.currentTimeMillis());
    }
}

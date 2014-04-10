package com.en_workshop.webcrawlerakka.akka.requests.other.status;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;

/**
 * Status query request
 *
 * @author Radu Ciumag
 */
public class StatusRequest extends MessageRequest {

    public StatusRequest() {
        super(System.currentTimeMillis());
    }
}

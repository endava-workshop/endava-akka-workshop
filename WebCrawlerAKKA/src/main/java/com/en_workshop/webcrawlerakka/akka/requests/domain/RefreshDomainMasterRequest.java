package com.en_workshop.webcrawlerakka.akka.requests.domain;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;

/**
 * @author Radu Ciumag
 */
public class RefreshDomainMasterRequest extends MessageRequest {

    public RefreshDomainMasterRequest() {
        super(System.currentTimeMillis());
    }
}

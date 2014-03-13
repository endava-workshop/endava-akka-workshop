package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.WebDomain;
import com.en_workshop.webcrawlerakka.entities.WebUrl;

/**
 *
 * Created by roxana on 3/13/14.
 */
public class PersistDomainRequest extends MessageRequest {

    private final WebDomain webDomain;

    public PersistDomainRequest(WebDomain webDomain) {
        super(System.currentTimeMillis());
        this.webDomain = webDomain;
    }

    public WebDomain getWebDomain() {
        return webDomain;
    }
}

package com.en_workshop.webcrawlerakka.akka.requests.other.status;

import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;
import com.en_workshop.webcrawlerakka.enums.ActorStatus;

/**
 * Status response
 *
 * @author Radu Ciumag
 */
public class StatusResponse extends MessageResponse {

    private final ActorStatus status;
    private final String message;

    public StatusResponse(final StatusRequest statusRequest, final ActorStatus status, final String message) {
        super(System.currentTimeMillis(), statusRequest);

        this.status = status;
        this.message = message;
    }

    public ActorStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

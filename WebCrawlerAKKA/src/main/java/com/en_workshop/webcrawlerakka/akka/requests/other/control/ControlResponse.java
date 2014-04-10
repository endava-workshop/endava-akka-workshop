package com.en_workshop.webcrawlerakka.akka.requests.other.control;

import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;
import com.en_workshop.webcrawlerakka.enums.ControlActionStatus;

/**
 * Control actors response
 *
 * @author Radu Ciumag
 */
public class ControlResponse extends MessageResponse {

    private final ControlActionStatus status;
    private final String message;

    public ControlResponse(final ControlRequest controlRequest, final ControlActionStatus status, final String message) {
        super(System.currentTimeMillis(), controlRequest);

        this.status = status;
        this.message = message;
    }

    public ControlActionStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

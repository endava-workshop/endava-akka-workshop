package com.en_workshop.webcrawlerakka.akka.requests.other.control.master;

import com.en_workshop.webcrawlerakka.akka.requests.other.control.ControlResponse;
import com.en_workshop.webcrawlerakka.enums.ControlActionStatus;

/**
 * Start MasterActor response
 *
 * @author Radu Ciumag
 */
public class ControlStartMasterResponse extends ControlResponse {

    public ControlStartMasterResponse(final ControlStartMasterRequest controlRequest, final ControlActionStatus status, final String message) {
        super(controlRequest, status, message);
    }
}

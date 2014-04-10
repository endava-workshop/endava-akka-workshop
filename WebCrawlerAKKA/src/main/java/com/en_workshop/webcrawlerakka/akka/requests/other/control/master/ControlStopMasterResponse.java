package com.en_workshop.webcrawlerakka.akka.requests.other.control.master;

import com.en_workshop.webcrawlerakka.akka.requests.other.control.ControlResponse;
import com.en_workshop.webcrawlerakka.enums.ControlActionStatus;

/**
 * Stop MasterActor response
 *
 * @author Radu Ciumag
 */
public class ControlStopMasterResponse extends ControlResponse {

    public ControlStopMasterResponse(final ControlStopMasterRequest controlRequest, final ControlActionStatus status, final String message) {
        super(controlRequest, status, message);
    }
}

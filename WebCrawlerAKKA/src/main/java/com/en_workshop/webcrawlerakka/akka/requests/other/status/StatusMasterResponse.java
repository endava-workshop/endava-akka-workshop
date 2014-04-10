package com.en_workshop.webcrawlerakka.akka.requests.other.status;

import com.en_workshop.webcrawlerakka.enums.ActorStatus;

/**
 * The status of MasterActor response
 *
 * @author Radu Ciumag
 */
public class StatusMasterResponse extends StatusResponse {

    public StatusMasterResponse(final StatusMasterRequest statusMasterRequest, final ActorStatus status, final String message) {
        super(statusMasterRequest, status, message);
    }
}

package com.en_workshop.webcrawlerakka.akka.requests.domain;

import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;

/**
 * @author Radu Ciumag
 */
public class DownloadUrlResponse extends MessageResponse {

    public DownloadUrlResponse(final DownloadUrlRequest downloadUrlRequest) {
        super(System.currentTimeMillis(), downloadUrlRequest);
    }

    public DownloadUrlRequest getDownloadUrlRequest() {
        return (DownloadUrlRequest) getMessageRequest();
    }
}

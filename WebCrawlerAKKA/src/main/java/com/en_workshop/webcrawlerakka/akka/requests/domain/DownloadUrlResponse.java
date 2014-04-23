package com.en_workshop.webcrawlerakka.akka.requests.domain;

import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;

/**
 * @author Radu Ciumag
 */
public class DownloadUrlResponse extends MessageResponse {

   private final boolean unresponsiveDomain;

    public DownloadUrlResponse(final DownloadUrlRequest downloadUrlRequest, boolean unresponsiveDomain) {
        super(System.currentTimeMillis(), downloadUrlRequest);
        this.unresponsiveDomain = unresponsiveDomain;
    }

    public DownloadUrlResponse(final DownloadUrlRequest downloadUrlRequest) {
        super(System.currentTimeMillis(), downloadUrlRequest);
        this.unresponsiveDomain = false;
    }

    public DownloadUrlRequest getDownloadUrlRequest() {
        return (DownloadUrlRequest) getMessageRequest();
    }

    public boolean isUnresponsiveDomain() {
        return unresponsiveDomain;
    }
}

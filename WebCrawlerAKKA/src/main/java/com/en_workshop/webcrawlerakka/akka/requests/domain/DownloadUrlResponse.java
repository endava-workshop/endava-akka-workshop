package com.en_workshop.webcrawlerakka.akka.requests.domain;

import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;
import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class DownloadUrlResponse extends MessageResponse {
    private static final Logger LOG = Logger.getLogger(DownloadUrlResponse.class);

    public DownloadUrlResponse(final DownloadUrlRequest downloadUrlRequest) {
        super(System.currentTimeMillis(), downloadUrlRequest);
    }

    public DownloadUrlRequest getDownloadUrlRequest() {
        return (DownloadUrlRequest) messageRequest;
    }
}

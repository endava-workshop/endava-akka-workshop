package com.en_workshop.webcrawlerakka.akka.requests;

import com.en_workshop.webcrawlerakka.entities.WebUrl;
import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class DownloadUrlRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(DownloadUrlRequest.class);

    private final WebUrl webUrl;

    public DownloadUrlRequest(final WebUrl webUrl) {
        super(System.currentTimeMillis());

        this.webUrl = webUrl;
    }

    public WebUrl getWebUrl() {
        return webUrl;
    }
}

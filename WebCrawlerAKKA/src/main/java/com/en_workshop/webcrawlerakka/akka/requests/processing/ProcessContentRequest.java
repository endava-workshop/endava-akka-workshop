package com.en_workshop.webcrawlerakka.akka.requests.processing;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.WebUrl;
import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class ProcessContentRequest extends MessageRequest {

    private static final Logger LOG = Logger.getLogger(ProcessContentRequest.class);

    private final WebUrl source;
    private final String content;

    public ProcessContentRequest(final WebUrl source, final String content) {
        super(System.currentTimeMillis());

        this.source = source;
        this.content = content;
    }

    public WebUrl getSource() {
        return source;
    }

    public String getContent() {
        return content;
    }
}

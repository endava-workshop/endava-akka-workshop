package com.en_workshop.webcrawlerakka.akka.actors.domain;

import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlResponse;
import com.en_workshop.webcrawlerakka.tools.WebClient;
import org.apache.http.Header;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Radu Ciumag
 */
public class DownloadUrlActor extends BaseActor {
    private static final Logger LOG = Logger.getLogger(DownloadUrlActor.class);

    /**
     * {@inheritDoc}
     */
    public void onReceive(Object message) {
        if (message instanceof DownloadUrlRequest) {
            final DownloadUrlRequest request = (DownloadUrlRequest) message;

            try {
                /* Test links mime types */
                final Header[] pageHeaders = WebClient.getPageHeaders(request.getWebUrl().getUrl());

                if (!WebClient.isMediaTypeAccepted(pageHeaders)) {
                    // TODO mark page as visited

                    return;
                }

                final String pageContent = WebClient.getPageContent(request.getWebUrl().getUrl());
                LOG.debug("Page content downloaded. Size (in characters): " + pageContent.length());


            } catch (IOException exc) {
                LOG.error("Cannot process link: " + request.getWebUrl().getUrl(), exc);
            }

            //TODO: Sent message to processing

            /* Identify the processing master actor and send it the work. */
//            Future<ActorRef> processingMasterSearch = getContext().actorSelection("akka://" + WebCrawlerConstants.SYSTEM_NAME + "/user/" + WebCrawlerConstants.MASTER_ACTOR_NAME + "/" +
//                    WebCrawlerConstants.PROCESSING_MASTER_ACTOR_NAME).resolveOne(Duration.create(1, TimeUnit.SECONDS));
//            processingMasterSearch.onSuccess(new OnSuccess<ActorRef>() {
//                @Override
//                public void onSuccess(ActorRef processingMasterActor) throws Throwable {
//                    processingMasterActor.tell(new ProcessContentRequest(request.getWebUrl(), "TODO - content"), getSelf());
//                }
//            }, getContext().dispatcher());
//            processingMasterSearch.onFailure(new OnFailure() {
//                @Override
//                public void onFailure(Throwable throwable) throws Throwable {
//                    LOG.error("Cannot identify the processing master actor. SOMETHING IS WRONG!!!");
//                }
//            }, getContext().dispatcher());

            /* Report back to the domain actor */
            DownloadUrlResponse response = new DownloadUrlResponse((DownloadUrlRequest) message);
            getSender().tell(response, getSelf());

            //TODO: Start actor to persist url and domain information
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}
package com.en_workshop.webcrawlerakka.akka.actors.domain;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlResponse;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import com.en_workshop.webcrawlerakka.dao.WebUrlDao;
import com.en_workshop.webcrawlerakka.enums.WebUrlStatus;
import com.en_workshop.webcrawlerakka.tools.WebClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

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
                final Map<String, String> pageHeaders = WebClient.getPageHeaders(request.getWebUrl().getUrl());

                /* Test the response code */
                if (!pageHeaders.get(WebCrawlerConstants.HTTP_CUSTOM_HEADER_RESPONSE_CODE).equals(WebCrawlerConstants.HTTP_RESPONSE_CODE_OK)) {
                    finishWork(request, WebUrlStatus.FAILED);
                    return;
                }

                /* Test for accepted mime types */
                if (!WebClient.isMediaTypeAccepted(pageHeaders.get(WebCrawlerConstants.HTTP_HEADER_CONTENT_TYPE))) {
                    finishWork(request, WebUrlStatus.VISITED);

                    return;
                }

                //TODO Test for redirects

                /* Get page content */
                final String pageContent = WebClient.getPageContent(request.getWebUrl().getUrl());
                LOG.debug("Page content downloaded. Size (in characters): " + pageContent.length());

                /* Send to processing master */
                findActor(WebCrawlerConstants.PROCESSING_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                            @Override
                            public void onSuccess(ActorRef processingMasterActor) throws Throwable {
                                processingMasterActor.tell(new ProcessContentRequest(request.getWebUrl(), pageContent), getSelf());
                            }
                        }, new OnFailure() {
                            @Override
                            public void onFailure(Throwable throwable) throws Throwable {
                                LOG.error("Cannot find Processing Master.");
                            }
                        }
                );

                finishWork(request, WebUrlStatus.VISITED);
            } catch (IOException exc) {
                LOG.error("Cannot process link: " + request.getWebUrl().getUrl(), exc);

                finishWork(request, WebUrlStatus.FAILED);
            }
        } else {
            LOG.error("Unknown message: " + message);
        }
    }

    /**
     * Finish the download actor work: persist link status; send the download url response
     *
     * @param request   The {@link com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlRequest}
     * @param urlStatus The new {@link com.en_workshop.webcrawlerakka.enums.WebUrlStatus}
     */
    private void finishWork(final DownloadUrlRequest request, final WebUrlStatus urlStatus) {
        /* Persist the new link status */
        // TODO Use the persistence master
        WebUrlDao.update(request.getWebUrl(), urlStatus);

        /* Report back to the domain actor */
        DownloadUrlResponse response = new DownloadUrlResponse(request);
        getSender().tell(response, getSelf());
    }
}
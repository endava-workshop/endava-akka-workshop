package com.en_workshop.webcrawlerakka.akka.actors.domain;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlResponse;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.enums.LinkStatus;
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
                final Map<String, String> pageHeaders = WebClient.getPageHeaders(request.getLink().getUrl());

                /* Test the response code */
                if (!pageHeaders.get(WebCrawlerConstants.HTTP_CUSTOM_HEADER_RESPONSE_CODE).equals(WebCrawlerConstants.HTTP_RESPONSE_CODE_OK)) {
                    LOG.debug(request.getLink().getUrl() + " - Response code not accepted: " + pageHeaders.get(WebCrawlerConstants.HTTP_CUSTOM_HEADER_RESPONSE_CODE));

                    finishWork(request, LinkStatus.FAILED);
                    return;
                }

                /* Test for accepted mime types */
                if (!WebClient.isMediaTypeAccepted(pageHeaders.get(WebCrawlerConstants.HTTP_HEADER_CONTENT_TYPE))) {
                    LOG.debug(request.getLink().getUrl() + " - Media type not accepted: " + pageHeaders.get(WebCrawlerConstants.HTTP_HEADER_CONTENT_TYPE));

                    finishWork(request, LinkStatus.VISITED);
                    return;
                }

                //TODO Test for redirects

                /* Get page content */
                final String pageContent = WebClient.getPageContent(request.getLink().getUrl());
                LOG.debug(request.getLink().getUrl() + " - Content downloaded (" + pageContent.length() + " chars)");

                /* Send to processing master */
                findLocalActor(WebCrawlerConstants.PROCESSING_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                            @Override
                            public void onSuccess(ActorRef processingMasterActor) throws Throwable {
                                processingMasterActor.tell(new ProcessContentRequest(request.getLink(), pageContent), getSelf());
                            }
                        }, new OnFailure() {
                            @Override
                            public void onFailure(Throwable throwable) throws Throwable {
                                LOG.error(request.getLink().getUrl() + " - Cannot find Processing Master");
                            }
                        }
                );

                finishWork(request, LinkStatus.VISITED);
            } catch (IOException exc) {
                LOG.error(request.getLink().getUrl() + " - Cannot process link", exc);

                finishWork(request, LinkStatus.FAILED);
            }
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }

    /**
     * Finish the download actor work: persist link status; send the download url response
     *
     * @param request   The {@link com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlRequest}
     * @param urlStatus The new {@link com.en_workshop.webcrawlerakka.enums.LinkStatus}
     */
    private void finishWork(final DownloadUrlRequest request, final LinkStatus urlStatus) {
        /* Persist the new link status */
        findLocalActor(WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                    @Override
                    public void onSuccess(ActorRef persistenceMasterActor) throws Throwable {
                        final Link newLink = new Link(request.getLink().getDomain(), request.getLink().getUrl(), urlStatus);

                        persistenceMasterActor.tell(new PersistLinkRequest(newLink), getSelf());
                    }
                }, new OnFailure() {
                    @Override
                    public void onFailure(Throwable throwable) throws Throwable {
                        LOG.error("Cannot find Persistence Master");
                    }
                }
        );

        /* Report back to the domain actor */
        DownloadUrlResponse response = new DownloadUrlResponse(request);
        getSender().tell(response, getSelf());
    }
}
package com.en_workshop.webcrawlerakka.akka.actors.domain;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlResponse;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.UpdateLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddLinkRequest;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.enums.LinkStatus;
import com.en_workshop.webcrawlerakka.tools.WebClient;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import scala.Option;

import java.io.IOException;
import java.util.Map;

/**
 * @author Radu Ciumag
 */
public class DownloadUrlActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    /**
     * {@inheritDoc}
     */
    public void onReceive(Object message) throws IOException {
        if (message instanceof DownloadUrlRequest) {
            final DownloadUrlRequest request = (DownloadUrlRequest) message;

            final Map<String, String> pageHeaders = WebClient.getPageHeaders(request.getLink().getUrl());

            /* Test the response code */
            if (!pageHeaders.get(WebCrawlerConstants.HTTP_CUSTOM_HEADER_RESPONSE_CODE).equals(WebCrawlerConstants.HTTP_RESPONSE_CODE_OK)) {
                LOG.debug(request.getLink().getUrl() + " - Response code not accepted: " + pageHeaders.get(WebCrawlerConstants.HTTP_CUSTOM_HEADER_RESPONSE_CODE));

                reportWork(request, LinkStatus.FAILED);
                return;
            }

            /* Test for accepted mime types */
            if (!WebClient.isMediaTypeAccepted(pageHeaders.get(WebCrawlerConstants.HTTP_HEADER_CONTENT_TYPE))) {
                LOG.debug(request.getLink().getUrl() + " - Media type not accepted: " + pageHeaders.get(WebCrawlerConstants.HTTP_HEADER_CONTENT_TYPE));

                reportWork(request, LinkStatus.VISITED);
                return;
            }

            doWork(request);
            reportWork(request, LinkStatus.VISITED);

        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }

    /**
     * Finish the download actor work: persist link status; send the download url response; send info to statistics
     *
     * @param request   The {@link com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlRequest}
     * @param urlStatus The new {@link com.en_workshop.webcrawlerakka.enums.LinkStatus}
     */
    private void reportWork(final DownloadUrlRequest request, final LinkStatus urlStatus, final boolean unresponsiveDomain) {
        final Link newLink = new Link(request.getLink().getDomain(), request.getLink().getSourceDomain(), request.getLink().getUrl(), request.getLink().getSourceLink(), urlStatus);
        /* Persist the new link status */
        getSender().tell(new UpdateLinkRequest(newLink), getSelf());

        /* Report back to the domain actor */
        DownloadUrlResponse response = new DownloadUrlResponse(request, unresponsiveDomain);
        getSender().tell(response, getSelf());

        /* Report to the statistics actor  */
        getSender().tell(new AddLinkRequest(request.getDomain().getName(), newLink), getSelf());
    }

    private void reportWork(final DownloadUrlRequest request, final LinkStatus urlStatus) {
        reportWork(request, urlStatus, false);
    }

    private void doWork(final DownloadUrlRequest request) throws IOException {
        /* Get page content */
        final String pageContent = WebClient.getPageContent(request.getLink().getUrl());
        LOG.debug(request.getLink().getUrl() + " - Content downloaded (" + pageContent.length() + " chars)");

        /* Send to processing master */
        getSender().tell(new ProcessContentRequest(request.getDomain(), request.getLink(), pageContent), getSelf());
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        final DownloadUrlRequest request = (DownloadUrlRequest) message.get();
        if (reason instanceof ClientProtocolException) {
            //mark the link as failed
            reportWork(request, LinkStatus.FAILED);
        } else if (reason instanceof ConnectTimeoutException) {
            //HttpHostConnectException
            reportWork(request, LinkStatus.NOT_VISITED, true);
        } else if (reason instanceof IOException) {
            //HttpHostConnectException is a subclass of IOException
            reportWork(request, LinkStatus.NOT_VISITED, true);
        }

        super.preRestart(reason, message);
    }
}
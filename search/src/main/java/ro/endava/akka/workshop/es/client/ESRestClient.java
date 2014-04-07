package ro.endava.akka.workshop.es.client;

import ro.endava.akka.workshop.es.actions.*;
import ro.endava.akka.workshop.es.responses.ESResponse;

/**
 * Created by cosmin on 4/6/14.
 * Interface for the rest client which will communicate with ES server
 */
public interface ESRestClient {

    /**
     * Send a request to ES server and waits for the result
     *
     * @param esAction
     * @return
     */
    ESResponse executeAsyncBlocking(ESAction esAction);

    /**
     * Send a request to ES server and does not block for the result(fire and forget)
     *
     * @param esAction
     */
    void executeAsyncNonBlocking(ESAction esAction);
}

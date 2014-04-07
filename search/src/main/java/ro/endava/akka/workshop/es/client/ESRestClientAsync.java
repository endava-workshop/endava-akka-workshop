package ro.endava.akka.workshop.es.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.endava.akka.workshop.es.actions.*;
import com.google.gson.Gson;
import ro.endava.akka.workshop.es.responses.ESResponse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by cosmin on 4/6/14.
 * Implementation for {@link ESRestClient}
 * <p>The communication is done using a {@link org.apache.http.impl.nio.client.CloseableHttpAsyncClient}</p>
 */
public class ESRestClientAsync implements ESRestClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(ESRestClientAsync.class);

    private String server;
    private Gson gson;
    private CloseableHttpAsyncClient asyncClient;

    public ESRestClientAsync(String server, Gson gson, CloseableHttpAsyncClient asyncClient) {
        this.server = server;
        this.gson = gson;
        this.asyncClient = asyncClient;
    }

    @Override
    public ESResponse executeAsyncBlocking(ESAction esAction) {
        HttpUriRequest request = buildRequest(esAction);
        asyncClient.start();
        Future<HttpResponse> future = asyncClient.execute(request, null);

        HttpResponse httpResponse = null;
        try {
            httpResponse = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void executeAsyncNonBlocking(ESAction esAction) {
        HttpUriRequest request = buildRequest(esAction);
        asyncClient.start();
        asyncClient.execute(request, new FutureCallback<HttpResponse>() {

            @Override
            public void completed(final HttpResponse response) {
                LOGGER.info("Successful async non blocking request");
                closeClient();
            }

            @Override
            public void failed(final Exception ex) {
                LOGGER.error("Error in request async non blocking: {}", ex);
                closeClient();
            }

            @Override
            public void cancelled() {
                LOGGER.info("Canceled async non blocking request");
                closeClient();
            }
        });
    }

    private HttpUriRequest buildRequest(ESAction esAction) {
        HttpUriRequest httpUriRequest = null;
        String url = buildUrl(esAction.getUrl());
        switch (esAction.getMethod()) {
            case "POST":
                httpUriRequest = new HttpPost(url);
                break;
            case "PUT":
                httpUriRequest = new HttpPut(url);
                break;
            case "GET":
                httpUriRequest = new HttpGet(url);
                break;
        }

        if (httpUriRequest != null &&
                httpUriRequest instanceof HttpEntityEnclosingRequestBase && esAction.getBody() != null) {
            ((HttpEntityEnclosingRequestBase) httpUriRequest).
                    setEntity(new StringEntity(createJsonStringEntity(esAction.getBody()),
                            Charset.forName("utf-8")));
        }

        return httpUriRequest;
    }


    private String createJsonStringEntity(Object data) {
        String entity;

        if (data instanceof String && isJson(data.toString())) {
            entity = data.toString();
        } else {
            entity = gson.toJson(data);
        }

        return entity;
    }

    private boolean isJson(String data) {
        try {
            JsonElement result = new JsonParser().parse(data);
            return !result.isJsonNull();
        } catch (JsonSyntaxException e) {
            //Check if this is a bulk request
            String[] bulkRequest = data.split("\n");
            return bulkRequest.length >= 1;
        }
    }


    private String buildUrl(String actionUrl) {
        StringBuilder sb = new StringBuilder(server);

        if (actionUrl.length() > 0 && actionUrl.charAt(0) == '/') sb.append(actionUrl);
        else sb.append('/').append(actionUrl);

        return sb.toString();
    }

    private void closeClient() {
        try {
            asyncClient.close();
        } catch (IOException e) {
            LOGGER.error("Error in closing client: {}", e);
        }
    }

    public String getServer() {
        return server;
    }

    public Gson getGson() {
        return gson;
    }

    public CloseableHttpAsyncClient getAsyncClient() {
        return asyncClient;
    }
}

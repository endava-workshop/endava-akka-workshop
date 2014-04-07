package ro.endava.akka.workshop.es.client;

import com.google.gson.Gson;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

/**
 * Created by cosmin on 4/6/14.
 * Factory for creating {@link ro.endava.akka.workshop.es.client.ESRestClient}
 */
public class ESRestClientFactory {

    public enum Type {
        ASYNC
    }

    public ESRestClient getClient(Type type, ESRestClientSettings settings) {
        String server = settings.getServer();
        if (server == null) {
            server = "http://localhost:9200";
        }
        Gson gson = settings.getGson();
        if (gson == null) {
            gson = new Gson();
        }

        ESRestClient esRestClient = null;
        switch (type) {
            case ASYNC:
                CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault();
                esRestClient = new ESRestClientAsync(server, gson, asyncClient);

        }
        return esRestClient;
    }
}

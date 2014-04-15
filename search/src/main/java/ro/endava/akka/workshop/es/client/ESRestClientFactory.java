package ro.endava.akka.workshop.es.client;

import com.google.gson.Gson;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.endava.akka.workshop.actors.IndexPasswordActor;

/**
 * Created by cosmin on 4/6/14.
 * Factory for creating {@link ro.endava.akka.workshop.es.client.ESRestClient}
 */
public class ESRestClientFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(ESRestClientFactory.class);

    public enum Type {
        ASYNC
    }
    
    private ESRestClient asynchRestClient;

    public synchronized ESRestClient getClient(Type type, boolean newInstance) {
    	LOGGER.debug("get client type : " + type + " | new instance : " + newInstance);
    	if(asynchRestClient != null && !newInstance){
    		return asynchRestClient;
    	}
    	
        ESRestClientSettings settings = ESRestClientSettings.builder().server("http://localhost:9200").build();

        String server = settings.getServer();
        if (server == null) {
            server = "http://localhost:9200";
        }
        Gson gson = settings.getGson();
        if (gson == null) {
            gson = new Gson();
        }

        switch (type) {
            case ASYNC:
                CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault();
                asyncClient.start();
                asynchRestClient = new ESRestClientAsync(server, gson, asyncClient);
                return asynchRestClient;
            default:
            	throw new RuntimeException("unimplemented solution for type : " + type);
        }
        
    }
}

package ro.endava.akka.workshop.actors;

import akka.actor.UntypedActor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import ro.endava.akka.workshop.dto.ESIndexResponse;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.BulkIndexMessage;
import ro.endava.akka.workshop.messages.IndexMessage;


/**
 * Created by cosmin on 3/10/14.
 * Actor that will index articles in elastic
 */
public class IndexArticleActor extends UntypedActor {
    private final static Logger LOGGER = LoggerFactory.getLogger(IndexArticleActor.class);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof IndexMessage) {
            IndexMessage indexMessage = (IndexMessage) message;
            LOGGER.info("Index Article Actor received an index request: " + indexMessage.toString());

            //After some tests, using the ES client takes 3 times more than using async calls with spring(1200-1500 ms vs 400-500 ms)
            //indexAsyncBlocking(indexMessage);
            indexAsyncNonBlocking(indexMessage);
            //indexAsyncWithCallback(indexMessage);
            //indexESClientNonBlocking(indexMessage);
            //indexESClientBlocking(indexMessage);

        } else {
            if (message instanceof BulkIndexMessage) {
                throw new ApplicationException("Not yet implemented.", ErrorCode.NOT_IMPLEMENTED);
            } else {
                throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
            }
        }
    }

    /**
     * Indexing using java ES client blocking, waiting for the result
     * @param indexMessage
     * @throws JsonProcessingException
     */
    private void indexESClientBlocking(IndexMessage indexMessage) throws JsonProcessingException {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearchsample")
                .build();
        TransportClient client = new TransportClient(settings);
        client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
        IndexResponse response = client.prepareIndex("articles", "article").
                setSource(new ObjectMapper().writeValueAsString(indexMessage)).execute().actionGet();
        LOGGER.info("[ES client blocking] Indexing successful : " + response.getIndex() + "/" + response.getType() + "/" + response.getId());
    }

    /**
     * Indexing using java ES client non-blocking, not waiting for result
     * @param indexMessage
     * @throws JsonProcessingException
     */
    private void indexESClientNonBlocking(IndexMessage indexMessage) throws JsonProcessingException {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearchsample")
                .build();
        TransportClient client = new TransportClient(settings);
        client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
        client.prepareIndex("articles", "article").
                setSource(new ObjectMapper().writeValueAsString(indexMessage)).execute();
        LOGGER.info("[ES client non-blocking] We just an index request to elastic server [fire and forget]");
    }


    /**
     * Indexing using  rest calls to elastic server, with Spring 4 AsyncRestTemplate, blocking,
     * waiting for the result
     * @param indexMessage
     * @throws Exception
     */
    private void indexAsyncBlocking(IndexMessage indexMessage) throws Exception {
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
        HttpEntity httpEntity = new HttpEntity(indexMessage);

        ListenableFuture<ResponseEntity<ESIndexResponse>> futureResponse =
                asyncRestTemplate.postForEntity("http://localhost:9200/articles/article", httpEntity, ESIndexResponse.class);
        ResponseEntity<ESIndexResponse> responseEntity = futureResponse.get();
        ESIndexResponse response = responseEntity.getBody();
        LOGGER.info("[Rest call blocking] Indexing successful : " + response);
    }

    /**
     * Indexing using  rest calls to elastic server, with Spring 4 AsyncRestTemplate, non- blocking,
     * not waiting for the result
     * @param indexMessage
     * @throws Exception
     */
    private void indexAsyncNonBlocking(IndexMessage indexMessage) throws Exception {
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
        HttpEntity httpEntity = new HttpEntity(indexMessage);

        ListenableFuture<ResponseEntity<ESIndexResponse>> futureResponse =
                asyncRestTemplate.postForEntity("http://localhost:9200/articles/article", httpEntity, ESIndexResponse.class);
        LOGGER.info("[Rest call non-blocking] We just an index request to elastic server [fire and forget]");
    }


    /**
     * Indexing using  rest calls to elastic server, with Spring 4 AsyncRestTemplate, with callback
     * @param indexMessage
     * @throws Exception
     */
    private void indexAsyncWithCallback(IndexMessage indexMessage) throws Exception {
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
        HttpEntity httpEntity = new HttpEntity(indexMessage);

        ListenableFuture<ResponseEntity<ESIndexResponse>> futureResponse =
                asyncRestTemplate.postForEntity("http://localhost:9200/articles/article", httpEntity, ESIndexResponse.class);

        futureResponse.addCallback(new ListenableFutureCallback<ResponseEntity<ESIndexResponse>>() {
            @Override
            public void onSuccess(ResponseEntity<ESIndexResponse> responseEntity) {
                ESIndexResponse response = responseEntity.getBody();
                LOGGER.info("[Rest call with callback] Indexing successful : " + response);
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.info("[Rest call with callback] Error indexing: " + throwable.getMessage());
            }
        });
    }
}

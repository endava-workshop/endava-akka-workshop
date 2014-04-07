package ro.endava.akka.workshop.actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.indices.Analyze;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import ro.endava.akka.workshop.dto.ESAnalyzeResponse;
import ro.endava.akka.workshop.es.actions.ESAbstractAction;
import ro.endava.akka.workshop.es.actions.ESAnalyzeAction;
import ro.endava.akka.workshop.es.actions.ESIndexAction;
import ro.endava.akka.workshop.es.client.ESRestClient;
import ro.endava.akka.workshop.es.client.ESRestClientFactory;
import ro.endava.akka.workshop.es.client.ESRestClientSettings;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.BulkIndexMessage;
import ro.endava.akka.workshop.messages.IndexMessage;
import ro.endava.akka.workshop.util.Transformer;

/**
 * Created by cosmin on 3/10/14.
 * Actor that will query elastic to get the tokens from an article
 */
public class IndexTokenizerActor extends UntypedActor {
    private final static Logger LOGGER = LoggerFactory.getLogger(IndexTokenizerActor.class);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof IndexMessage) {
            IndexMessage indexMessage = (IndexMessage) message;
            LOGGER.info("Index Tokenizer Actor received a tokenizer request: " + indexMessage.toString());
            tokenizeAsyncWithCallback(indexMessage);
            tokenizeBlocking(indexMessage);
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }

    private void tokenizeBlocking(IndexMessage indexMessage) throws Exception {
        // Configuration
        ClientConfig clientConfig = new ClientConfig.Builder("http://localhost:9200").multiThreaded(true).build();

        // Construct a new Jest client according to configuration via factory
        JestClientFactory jestClientFactory = new JestClientFactory();
        jestClientFactory.setClientConfig(clientConfig);
        JestClient jestClient = jestClientFactory.getObject();
        Analyze analyze = new Analyze.Builder().index("analyzerindex").source(indexMessage.getContent()).
                analyzer("myanalyzer").build();
        JestResult execute = jestClient.execute(analyze);
        ESRestClientSettings settings = ESRestClientSettings.builder().server("http://localhost:9200").build();
        ESRestClientFactory factory = new ESRestClientFactory();
        ESRestClient client = factory.getClient(ESRestClientFactory.Type.ASYNC, settings);

        ESAnalyzeAction analyzeAction = new ESAnalyzeAction.Builder().index("analyzerindex").
                analyzer("myanalyzer").source(indexMessage.getContent()).build();

        client.executeAsyncBlocking(analyzeAction);
    }

    /**
     * Calling _analyze using  rest calls to elastic server, with Spring 4 AsyncRestTemplate, with callback,
     * to get the tokens from the content
     *
     * @param indexMessage
     * @throws Exception
     */
    private void tokenizeAsyncWithCallback(IndexMessage indexMessage) throws Exception {
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();

        String url = "http://localhost:9200/analyzerindex/_analyze?analyzer=myanalyzer&text=" + indexMessage.getContent();
        ListenableFuture<ResponseEntity<ESAnalyzeResponse>> futureResponse =
                asyncRestTemplate.getForEntity(url, ESAnalyzeResponse.class);

        futureResponse.addCallback(new ListenableFutureCallback<ResponseEntity<ESAnalyzeResponse>>() {
            @Override
            public void onSuccess(ResponseEntity<ESAnalyzeResponse> responseEntity) {
                ESAnalyzeResponse response = responseEntity.getBody();
                LOGGER.info("[Tokenizer with callback] Tokens successfully retrieved : " + response);
                forwardTokens(response);
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.info("[Tokenizer with callback] Error indexing: " + throwable.getMessage());
            }
        });
    }

    /**
     * Sends message with the tokens to be indexed as passwords to IndexPasswordActor
     *
     * @param analyzeResponse
     */
    private void forwardTokens(ESAnalyzeResponse analyzeResponse) {
        this.getContext().actorOf(Props.create(IndexPasswordActor.class)).
                tell(Transformer.tokensToPasswords(analyzeResponse), getSelf());
    }
}

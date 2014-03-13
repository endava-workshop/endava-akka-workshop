package ro.endava.akka.workshop.actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import ro.endava.akka.workshop.dto.ESAnalyzeResponse;
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
        } else {
            if (message instanceof BulkIndexMessage) {
                throw new ApplicationException("Not yet implemented.", ErrorCode.NOT_IMPLEMENTED);
            } else {
                throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
            }
        }
    }

    /**
     * Calling _analyze using  rest calls to elastic server, with Spring 4 AsyncRestTemplate, blocking,
     * waiting for the result, to get the tokens from the content
     *
     * @param indexMessage
     * @throws Exception
     */
    private void tokenizeAsyncBlocking(IndexMessage indexMessage) throws Exception {
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();

        String url = "http://localhost:9200/analyzerindex/_analyze?analyzer=myanalyzer&text=" + indexMessage.getContent();
        ListenableFuture<ResponseEntity<ESAnalyzeResponse>> futureResponse =
                asyncRestTemplate.getForEntity(url, ESAnalyzeResponse.class);
        ResponseEntity<ESAnalyzeResponse> responseEntity = futureResponse.get();
        ESAnalyzeResponse response = responseEntity.getBody();
        LOGGER.info("[Tokenizer Async blocking] Tokens successfully retrieved : " + response);
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
     * For now just a password
     * @param analyzeResponse
     */
    private void forwardTokens(ESAnalyzeResponse analyzeResponse) {
        this.getContext().actorOf(Props.create(IndexPasswordActor.class)).
                tell(Transformer.tokenToPassword(analyzeResponse.getTokens().get(0)), getSelf());
    }
}

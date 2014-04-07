package ro.endava.akka.workshop.actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.endava.akka.workshop.es.responses.ESAnalyzeResponse;
import ro.endava.akka.workshop.es.actions.ESAnalyzeAction;
import ro.endava.akka.workshop.es.client.ESRestClient;
import ro.endava.akka.workshop.es.client.ESRestClientFactory;
import ro.endava.akka.workshop.es.client.ESRestClientSettings;
import ro.endava.akka.workshop.es.responses.ESResponse;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
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
            tokenizeBlocking(indexMessage);
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }

    /**
     * Sends request to ES for getting passwords as tokens from an article
     *
     * @param indexMessage
     * @throws Exception
     */
    private void tokenizeBlocking(IndexMessage indexMessage) throws Exception {
        ESRestClientSettings settings = ESRestClientSettings.builder().server("http://localhost:9200").build();
        ESRestClientFactory factory = new ESRestClientFactory();
        ESRestClient client = factory.getClient(ESRestClientFactory.Type.ASYNC, settings);

        ESAnalyzeAction analyzeAction = new ESAnalyzeAction.Builder().index("analyzerindex").
                analyzer("myanalyzer").body(indexMessage.getContent()).build();

        ESResponse esResponse = client.executeAsyncBlocking(analyzeAction);
        LOGGER.info("[ES client blocking] Index Tokenizer Actor successfully tokenized passwords");
        ESAnalyzeResponse analyzeResponse = esResponse.getSourceAsObject(ESAnalyzeResponse.class);
        forwardTokens(analyzeResponse);
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

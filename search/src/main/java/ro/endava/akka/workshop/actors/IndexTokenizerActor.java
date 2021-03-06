package ro.endava.akka.workshop.actors;

import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.endava.akka.workshop.es.actions.ESAnalyzeAction;
import ro.endava.akka.workshop.es.client.ESRestClient;
import ro.endava.akka.workshop.es.responses.ESAnalyzeResponse;
import ro.endava.akka.workshop.es.responses.ESResponse;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.BulkPasswordMessage;
import ro.endava.akka.workshop.messages.IndexMessage;
import ro.endava.akka.workshop.util.Transformer;

/**
 * Created by cosmin on 3/10/14.
 * Actor that will query elastic to get the tokens from an article
 */
public class IndexTokenizerActor extends UntypedActor {

    private final static Logger LOGGER = LoggerFactory.getLogger(IndexTokenizerActor.class);

    private ESRestClient client;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof IndexMessage) {
            tokenizeAndRespond((IndexMessage) message);
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }

    /**
     * Sends request to ES for getting passwords as tokens from an article
     * Responding to sender
     *
     * @param indexMessage
     * @throws Exception
     */
    private void tokenizeAndRespond(IndexMessage indexMessage) throws Exception {
        ESAnalyzeAction analyzeAction = new ESAnalyzeAction.Builder().index("analysis").
                analyzer("myanalyzer").body(indexMessage.getContent()).build();

        ESResponse esResponse = client.executeAsyncBlocking(analyzeAction);
        ESAnalyzeResponse analyzeResponse = esResponse.getSourceAsObject(ESAnalyzeResponse.class);
        BulkPasswordMessage bulkPasswordMessage = Transformer.tokensToPasswords(analyzeResponse);
        getSender().tell(bulkPasswordMessage, getSelf());
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        client = ESRestClient.getInstance();
    }
}

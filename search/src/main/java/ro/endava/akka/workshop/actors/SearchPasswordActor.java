package ro.endava.akka.workshop.actors;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.endava.akka.workshop.es.actions.ESSearchAction;
import ro.endava.akka.workshop.es.client.ESRestClient;
import ro.endava.akka.workshop.es.client.ESRestClientFactory;
import ro.endava.akka.workshop.es.responses.ESResponse;
import ro.endava.akka.workshop.es.responses.custom.PasswordHits;
import ro.endava.akka.workshop.es.responses.custom.PasswordSearchResponse;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.SearchPasswordHitsMessage;
import ro.endava.akka.workshop.messages.SearchPasswordReqMessage;
import akka.actor.UntypedActor;

/**
 * Created by cosmin on 3/10/14.
 * Actor that will search in ES for passwords and retrieve them
 */
public class SearchPasswordActor extends UntypedActor {
    private final static Logger LOGGER = LoggerFactory.getLogger(SearchRouterActor.class);

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof SearchPasswordReqMessage) {
        	LOGGER.debug("received a SearchPasswordReqMessage");

            searchPasswords((SearchPasswordReqMessage) message);
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }

    private void searchPasswords(SearchPasswordReqMessage message) {
        ESRestClientFactory factory = new ESRestClientFactory();
        ESRestClient client = factory.getClient(ESRestClientFactory.Type.ASYNC, false);

        ESSearchAction searchAction = new ESSearchAction.Builder()
                .index("passwords").type(message.getPasswordType().toString()).from(message.getFrom()).size(message.getSize()).build();
        ESResponse esResponse = client.executeAsyncBlocking(searchAction);

        PasswordSearchResponse searchResponse = esResponse.getSourceAsObject(PasswordSearchResponse.class);
        List<String> passwords = new ArrayList<>();
        if (searchResponse != null && searchResponse.getHits().getHits() != null && searchResponse.getHits().getHits() != null) {
            for (PasswordHits passwordHits : searchResponse.getHits().getHits()) {
                passwords.add(passwordHits.get_source().getPassword());
            }
        }
        SearchPasswordHitsMessage resultMessage = new SearchPasswordHitsMessage(message.getSender(), passwords);
        getSender().tell(resultMessage, getSelf());

    }
}

package ro.endava.akka.workshop.actors;

import akka.actor.UntypedActor;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.endava.akka.workshop.es.actions.ESSearchAction;
import ro.endava.akka.workshop.es.client.ESRestClient;
import ro.endava.akka.workshop.es.client.ESRestClientFactory;
import ro.endava.akka.workshop.es.client.ESRestClientSettings;
import ro.endava.akka.workshop.es.responses.ESResponse;
import ro.endava.akka.workshop.es.responses.custom.PasswordHits;
import ro.endava.akka.workshop.es.responses.custom.PasswordSearchResponse;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.SearchPasswordMessage;
import ro.endava.akka.workshop.messages.SearchPasswordResultMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cosmin on 3/10/14.
 * Actor that will search in ES for passwords and retrieve them
 */
public class SearchPasswordActor extends UntypedActor {

    private final static Logger LOGGER = LoggerFactory.getLogger(SearchPasswordActor.class);

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof SearchPasswordMessage) {
            SearchPasswordMessage searchPasswordMessage = (SearchPasswordMessage) message;
            LOGGER.info("Search Password Actor received a message: " + searchPasswordMessage);
            searchPassword(searchPasswordMessage);
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }

    private void searchPassword(SearchPasswordMessage message) {
        ESRestClientSettings settings = ESRestClientSettings.builder().server("http://localhost:9200").build();
        ESRestClientFactory factory = new ESRestClientFactory();
        ESRestClient client = factory.getClient(ESRestClientFactory.Type.ASYNC, settings);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.queryString("gigi"));
        ESSearchAction searchAction = new ESSearchAction.Builder()
                .index("passwords").type("password").from(0L).size(2L).build();
        ESResponse esResponse = client.executeAsyncBlocking(searchAction);

        PasswordSearchResponse searchResponse = esResponse.getSourceAsObject(PasswordSearchResponse.class);
        List<String> passwords = new ArrayList<>();
        for (PasswordHits passwordHits : searchResponse.getHits().getHits()) {
            passwords.add(passwordHits.get_source().getPassword());
        }

        SearchPasswordResultMessage resultMessage = new SearchPasswordResultMessage(message.getSender(), passwords);
        getSender().tell(resultMessage, getSelf());

    }
}

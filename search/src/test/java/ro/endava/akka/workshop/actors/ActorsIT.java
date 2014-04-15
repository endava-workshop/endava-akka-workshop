package ro.endava.akka.workshop.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import junit.framework.TestCase;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ro.endava.akka.workshop.es.actions.ESCreateIndexAction;
import ro.endava.akka.workshop.es.actions.ESIsIndexAction;
import ro.endava.akka.workshop.es.actions.ESPutMappingAction;
import ro.endava.akka.workshop.es.actions.structures.ESAnalyzer;
import ro.endava.akka.workshop.es.actions.structures.ESFilter;
import ro.endava.akka.workshop.es.client.ESRestClient;
import ro.endava.akka.workshop.es.client.ESRestClientFactory;
import ro.endava.akka.workshop.es.responses.ESIndexResponse;
import ro.endava.akka.workshop.es.responses.ESResponse;
import ro.endava.akka.workshop.messages.IndexMessage;
import ro.endava.akka.workshop.messages.SearchPasswordMessage;
import ro.endava.akka.workshop.messages.SearchPasswordResultMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cosmin on 3/10/14.
 */
public class ActorsIT {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testIndex() {
        new JavaTestKit(system) {{
            final Props props = Props.create(IndexDispatcherActor.class);
            final ActorRef subject = system.actorOf(props);

            // the run() method needs to finish within 10 seconds
            new Within(duration("10 seconds")) {
                protected void run() {
                    subject.tell(createArticle1(), getRef());
                    // Will wait for the rest of the 10 seconds
                    expectNoMsg();
                }
            };
        }};
    }

    @Test
    public void testSearchPassword() {
        new JavaTestKit(system) {{
            final Props props = Props.create(SearchRouterActor.class);
            final ActorRef subject = system.actorOf(props);

            // the run() method needs to finish within 10 seconds
            new Within(duration("10 seconds")) {
                protected void run() {
                    subject.tell(createSearchPasswordMessage(getRef()), getRef());
                    // Will wait for the rest of the 10 seconds
                    expectMsgClass(SearchPasswordResultMessage.class);
                }
            };
        }};
    }

    /**
     * Integration test for testing creating an index. Needs ES working
     */
    @Test
    public void testCreateIndex() {
        ESRestClientFactory factory = new ESRestClientFactory();
        ESRestClient client = factory.getClient(ESRestClientFactory.Type.ASYNC, false);
        ESCreateIndexAction createIndexAction = new ESCreateIndexAction.Builder().index("randomindex").build();
        ESResponse createIndexResponse = client.executeAsyncBlocking(createIndexAction);
        ESIndexResponse indexResponse = createIndexResponse.getSourceAsObject(ESIndexResponse.class);
        TestCase.assertTrue(indexResponse.getAcknowledged());

        ESPutMappingAction mappingAction = new ESPutMappingAction.Builder().index("randomindex").type("randomtype").
                attribute("attr1", "string").build();
        client.executeAsyncBlocking(mappingAction);
    }


    /**
     * Integration test for testing creating an index with settings. Needs ES working
     */
    @Test
    public void testCreateIndexWithSettings() {
        ESRestClientFactory factory = new ESRestClientFactory();
        ESRestClient client = factory.getClient(ESRestClientFactory.Type.ASYNC, false);

        Map<String, Object> props = new HashMap<>();
        props.put("type", "custom");
        props.put("tokenizer", "standard");
        List<String> filter = new ArrayList<>();
        filter.add("stop_words");
        props.put("filter", filter);
        ESAnalyzer esAnalyzer = new ESAnalyzer("myanalyzer", props);

        Map<String, Object> filterProps = new HashMap<>();
        filterProps.put("type", "stop");
        filterProps.put("ignore_case", true);
        List<String> stopWords = new ArrayList<>();
        stopWords.add("ce");
        filterProps.put("stopwords", stopWords);
        ESFilter esFilter = new ESFilter("stop_words", filterProps);

        ESCreateIndexAction createIndexAction = new ESCreateIndexAction.Builder().index("randomindex").analyzer(esAnalyzer).filter(esFilter).build();
        ESResponse createIndexResponse = client.executeAsyncBlocking(createIndexAction);
        ESIndexResponse indexResponse = createIndexResponse.getSourceAsObject(ESIndexResponse.class);
        TestCase.assertTrue(indexResponse.getAcknowledged());

        ESPutMappingAction mappingAction = new ESPutMappingAction.Builder().index("randomindex").type("randomtype").
                attribute("attr1", "string").build();
        client.executeAsyncBlocking(mappingAction);
    }


    @Test
    public void testIsIndex() {
        ESRestClientFactory factory = new ESRestClientFactory();
        ESRestClient client = factory.getClient(ESRestClientFactory.Type.ASYNC, false);
        ESIsIndexAction isIndexAction = new ESIsIndexAction.Builder().index("mama").build();
        ESResponse esResponse = client.executeAsyncBlocking(isIndexAction);
        Assert.assertFalse(esResponse.isOk());
    }

    private IndexMessage createArticle1() {
        String domain = "domain";
        String content = "Buna ziua, ce mai faceti? si cand mai veniti pe la noi?";
        IndexMessage indexMessage = new IndexMessage(domain, content);
        return indexMessage;
    }

    private SearchPasswordMessage createSearchPasswordMessage(ActorRef actorRef) {
        return new SearchPasswordMessage(actorRef);
    }
}

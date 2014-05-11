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
import ro.endava.akka.workshop.es.responses.ESIndexResponse;
import ro.endava.akka.workshop.es.responses.ESResponse;
import ro.endava.akka.workshop.messages.*;

import java.util.*;

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
            new Within(duration("20000 seconds")) {
                protected void run() {
                    for(int i=0; i<5000; i++){
                        subject.tell(createSearchPasswordMessage(), getRef());
                    }
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
        ESRestClient client = ESRestClient.getInstance();
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
        ESRestClient client = ESRestClient.getInstance();

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
        ESRestClient client = ESRestClient.getInstance();
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

    private SearchPasswordMessage createSearchPasswordMessage() {
        Random random = new Random();
        int randomNum = random.nextInt((4000 - 0) + 1) + 0;
        return new SearchPasswordMessage(new Integer(randomNum).longValue(), new Integer(randomNum).longValue() + 1000);
    }
}

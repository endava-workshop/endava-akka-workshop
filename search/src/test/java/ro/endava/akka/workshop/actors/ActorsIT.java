package ro.endava.akka.workshop.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ro.endava.akka.workshop.messages.IndexMessage;
import ro.endava.akka.workshop.messages.SearchPasswordMessage;
import ro.endava.akka.workshop.messages.SearchPasswordResultMessage;

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

package ro.endava.akka.workshop.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ro.endava.akka.workshop.messages.IndexMessage;

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
    public void mainTest() {
        new JavaTestKit(system) {{
            final Props props = Props.create(IndexDispatcherActor.class);
            final ActorRef subject = system.actorOf(props);

            // the run() method needs to finish within 3 seconds
            new Within(duration("60 seconds")) {
                protected void run() {
                    subject.tell(createDummyRequest(), getRef());
                    // Will wait for the rest of the 3 seconds
                    expectNoMsg();
                }
            };
        }};
    }

    private IndexMessage createDummyRequest() {
        String domain = "domain";
        String content = "Buna ziua, ce mai faceti? si cand mai veniti pe la noi?";
        IndexMessage indexMessage = new IndexMessage(domain, content);
        return indexMessage;
    }
}

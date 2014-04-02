package akka.ws.pass.breaker.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.testkit.JavaTestKit;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.RequestPasswordFlowMessage;
import akka.ws.pass.breaker.messages.RequestTotalSpentTimeMessage;
import akka.ws.pass.breaker.messages.TotalSpentTimeMessage;
import scala.concurrent.duration.FiniteDuration;

import org.junit.Before;
import org.junit.Test;

/**
 * Test methods of this class succeed only if the are ran individually.
 * 
 * @author ddoboga
 *
 */
public class PasswordProviderPerformanceTest {

	private ActorSystem actorSystem;
	
	private static final FiniteDuration TIMEOUT = JavaTestKit.duration("100 second");
	private static final int EXPECTED_TOTAL_PASSWORDS = 10000;
	private static final int CHUNK_SIZE = PasswordProvider.PASSWORD_CHUNK_SIZE;
	private static final int NO_OF_CHUNKS = EXPECTED_TOTAL_PASSWORDS / CHUNK_SIZE;
	
	@Before
	public void setUp() throws Exception {
		actorSystem = ActorSystem.create("LocalZipBreakerActorSystem");
	}

	@Test
	public void testContinueProcessFlow() {
		
		new JavaTestKit(actorSystem) {
			{
				final ActorRef passwordProviderActor = actorSystem.actorOf(Props.create(PasswordProvider.class));
				final long processId = 8474;

				RequestPasswordFlowMessage startMessage = new RequestPasswordFlowMessage(processId);
				EndProcessMessage endProcessMessage = new EndProcessMessage(processId);
				RequestTotalSpentTimeMessage requestTimeInfo = new RequestTotalSpentTimeMessage();

				//start the process
				passwordProviderActor.tell(startMessage, getRef());
				
				//assert the actor sends at least 100 messages in less than 5 seconds
				receiveN(NO_OF_CHUNKS, TIMEOUT);

				passwordProviderActor.tell(endProcessMessage, getRef());
				
				try {
					final int largeEnoughNumber = Integer.MAX_VALUE;
					receiveN(largeEnoughNumber);
				} catch (java.lang.AssertionError e1) {
					// Don't care how many it receives. I just want to consume all that are on the go
				}
				
				passwordProviderActor.tell(requestTimeInfo, getRef());
				TotalSpentTimeMessage timeInfo = expectMsgClass(TIMEOUT, TotalSpentTimeMessage.class);
				LoggingAdapter log = Logging.getLogger(actorSystem, this);
				log.info("\n********* Total CPU time consumed while generating " + EXPECTED_TOTAL_PASSWORDS + " passwords organized in " + NO_OF_CHUNKS + " chunks: " + timeInfo.getTotalCPUTimeMillis() + " milliseconds.");
			}
		};
	}

}

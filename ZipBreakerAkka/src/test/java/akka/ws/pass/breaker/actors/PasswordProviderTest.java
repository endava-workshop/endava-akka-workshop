package akka.ws.pass.breaker.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.ws.pass.breaker.messages.ContinuePasswordFlowMessage;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.PasswordChunkMessage;
import akka.ws.pass.breaker.messages.RequestPasswordFlowMessage;
import scala.concurrent.duration.FiniteDuration;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Test methods of this class succeed only if the are ran individually.
 * 
 * @author ddoboga
 *
 */
public class PasswordProviderTest {

	private ActorSystem actorSystem;
	
	private static final FiniteDuration TIMEOUT = JavaTestKit.duration("5 second");
	
	@Before
	public void setUp() throws Exception {
		actorSystem = ActorSystem.create("LocalZipBreakerActorSystem");
	}

	@Test
	public void testRequestPasswordFlow() {
		/*
		 * Wrap the whole test procedure within a testkit constructor if you
		 * want to receive actor replies or use Within(), etc.
		 */
		new JavaTestKit(actorSystem) {
			{
				final ActorRef passwordProviderActor = actorSystem.actorOf(Props.create(PasswordProvider.class));
				final long processId = 8474; //some magic number (I don't care)

				RequestPasswordFlowMessage inMessage = new RequestPasswordFlowMessage(processId);
				passwordProviderActor.tell(inMessage, getRef());

				PasswordChunkMessage outMessage = expectMsgClass(TIMEOUT, PasswordChunkMessage.class);
				
				assertEquals(processId, outMessage.getProcessId());
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testContinueProcessFlow() {
		
		new JavaTestKit(actorSystem) {
			{
				final ActorRef passwordProviderActor = actorSystem.actorOf(Props.create(PasswordProvider.class));
				final long processId = 8474;

				RequestPasswordFlowMessage startMessage = new RequestPasswordFlowMessage(processId);

				//start the process
				passwordProviderActor.tell(startMessage, getRef());
				
				//assert the actor sends at least 100 messages in less than 5 seconds
				receiveN(100, TIMEOUT);

				expectMsgAnyClassOf(PasswordChunkMessage.class, ContinuePasswordFlowMessage.class);
			}
		};
	}

	@Test
	public void testEndProcess() {
		
		new JavaTestKit(actorSystem) {
			{
				final ActorRef passwordProviderActor = actorSystem.actorOf(Props.create(PasswordProvider.class));
				final long processId = 8474; //some magic number (I don't care)

				RequestPasswordFlowMessage startMessage = new RequestPasswordFlowMessage(processId);
				EndProcessMessage endProcessMessage = new EndProcessMessage(processId);

				//start the process
				passwordProviderActor.tell(startMessage, getRef());

				//announce the actor that the process should stop
				passwordProviderActor.tell(endProcessMessage, getRef());
				
				//assert the actor sends at least 100 messages in less than 5 seconds
				try {
					receiveN(10, TIMEOUT);
					fail("It should have not received so many messages. It seems the proces has not been ended!");
				} catch(AssertionError e) {
					//OK! If no more than 10 messages are received in the same interval in which the testContinueProcessFlow() has received more than 100, it means the process has stopped
				}

			}
		};
	}

}

package akka.ws.pass.breaker.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.FoundPasswordMessage;
import akka.ws.pass.breaker.messages.StartNewProcessMessage;
import scala.concurrent.duration.FiniteDuration;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * 
 * @author ddoboga
 *
 */
public class PasswordCheckerTest {

	private ActorSystem actorSystem;
	private static final String TEST_FILE_NAME = "experiment.zip";
	private static final String THE_PASSWORD = "o parola";
	private static final FiniteDuration TIMEOUT = JavaTestKit.duration("5 second");
	
	@Before
	public void setUp() throws Exception {
		actorSystem = ActorSystem.create("LocalZipBreakerActorSystem");
	}
	
	@After
	public void tearDown() throws Exception {
		actorSystem.shutdown();
		//allow enough time to shutdown
		Thread.sleep(100);
	}

	@Test
	public void testFindPassword() {
		
		new JavaTestKit(actorSystem) {
			{
				final ActorRef passwordCheckerActor = actorSystem.actorOf(Props.create(PasswordChecker.class));
				final long processId = 8474;
				final File zipArchiveFile = provideTestFile();

				StartNewProcessMessage startProcessMessage = new StartNewProcessMessage(processId, zipArchiveFile);
				FeedProcessMessage feedProcessMessage = passwordsChunk(processId, "128347", "something", "something_else", THE_PASSWORD, "and other");
				
				passwordCheckerActor.tell(startProcessMessage, getRef());
				passwordCheckerActor.tell(feedProcessMessage, getRef());

				FoundPasswordMessage outMessage = expectMsgClass(TIMEOUT, FoundPasswordMessage.class);
				
				assertEquals(processId, outMessage.getProcessId());
				assertEquals(THE_PASSWORD, outMessage.getSuccessfullPassword());
			}
		};
	}

	@Test
	public void testWrongPasswordsOnly() {
		
		new JavaTestKit(actorSystem) {
			{
				final ActorRef passwordCheckerActor = actorSystem.actorOf(Props.create(PasswordChecker.class));
				final long processId = 8474;
				final File zipArchiveFile = provideTestFile();

				StartNewProcessMessage startProcessMessage = new StartNewProcessMessage(processId, zipArchiveFile);
				FeedProcessMessage feedProcessMessage = passwordsChunk(processId, "128347", "something", "something_else", "o parola gresita", "and other");
				
				passwordCheckerActor.tell(startProcessMessage, getRef());
				passwordCheckerActor.tell(feedProcessMessage, getRef());

				expectNoMsg(TIMEOUT);
			}
		};
	}

	@Test
	public void testEndProcess() {
		
		new JavaTestKit(actorSystem) {
			{
				final ActorRef passwordCheckerActor = actorSystem.actorOf(Props.create(PasswordChecker.class));
				final long processId = 8474;
				final File zipArchiveFile = provideTestFile();

				StartNewProcessMessage startProcessMessage = new StartNewProcessMessage(processId, zipArchiveFile);
				FeedProcessMessage wrongPasswordsChunk = passwordsChunk(processId, "wrong password 1", "mike", "kitty", "Nathan", "Peter Pan");
				FeedProcessMessage successfullPasswordsChunk = passwordsChunk(processId, "128347", "something", "something_else", THE_PASSWORD, "and other");
				EndProcessMessage endProcessMessage = new EndProcessMessage(processId);
				
				passwordCheckerActor.tell(startProcessMessage, getRef());
				passwordCheckerActor.tell(wrongPasswordsChunk, getRef());
				passwordCheckerActor.tell(endProcessMessage, getRef());
				passwordCheckerActor.tell(successfullPasswordsChunk, getRef());

				expectNoMsg(TIMEOUT);
			}
		};
	}
	
	private File provideTestFile() {
		final URL resource = this.getClass().getClassLoader().getResource(TEST_FILE_NAME);
		final String fullFilePath = resource.getPath();
		return new File(fullFilePath);
	}
	
	private FeedProcessMessage passwordsChunk(Long processId, String... passwords) {
		FeedProcessMessage message = new FeedProcessMessage(Arrays.asList(passwords), processId);
		return message;
	}

}

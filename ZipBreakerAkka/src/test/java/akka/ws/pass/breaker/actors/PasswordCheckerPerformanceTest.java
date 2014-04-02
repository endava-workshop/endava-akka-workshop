package akka.ws.pass.breaker.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.testkit.JavaTestKit;
import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.FoundPasswordMessage;
import akka.ws.pass.breaker.messages.RequestTotalSpentTimeMessage;
import akka.ws.pass.breaker.messages.StartNewProcessMessage;
import akka.ws.pass.breaker.messages.TotalSpentTimeMessage;
import scala.concurrent.duration.FiniteDuration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * 
 * @author ddoboga
 *
 */
public class PasswordCheckerPerformanceTest {

	private ActorSystem actorSystem;
	private static final String TEST_FILE_NAME = "experiment.zip";
	private static final String THE_PASSWORD = "o parola";
	private static final FiniteDuration TIMEOUT = JavaTestKit.duration("100 second");
	private static final String PASSWORD_SEED = "some pass ";

	private Random random = new Random();
	
	@Before
	public void setUp() throws Exception {
		actorSystem = ActorSystem.create("LocalZipBreakerActorSystem");
	}

	@Test
	public void testFindPassword() {
		
		new JavaTestKit(actorSystem) {
			{
				final ActorRef passwordCheckerActor = actorSystem.actorOf(Props.create(PasswordChecker.class));
				final long processId = 8474;
				final int chunkSize = 10000;
				final File zipArchiveFile = provideTestFile();

				StartNewProcessMessage startProcessMessage = new StartNewProcessMessage(processId, zipArchiveFile);
				FeedProcessMessage feedProcessMessage = passwordsChunk(processId, chunkSize);
				
				passwordCheckerActor.tell(startProcessMessage, getRef());
				passwordCheckerActor.tell(feedProcessMessage, getRef());

				FoundPasswordMessage outMessage = expectMsgClass(TIMEOUT, FoundPasswordMessage.class);
				assertEquals(THE_PASSWORD, outMessage.getSuccessfullPassword());
				
				RequestTotalSpentTimeMessage requestTimeInfo = new RequestTotalSpentTimeMessage();
				passwordCheckerActor.tell(requestTimeInfo, getRef());
				
				TotalSpentTimeMessage timeInfo = expectMsgClass(TIMEOUT, TotalSpentTimeMessage.class);
				LoggingAdapter log = Logging.getLogger(actorSystem, this);
				log.info("\n********* Total spent CPU time while checking " + chunkSize + " passwords in one chunk : " + timeInfo.getTotalCPUTimeMillis() + " milliseconds.");
			}
		};
	}
	
	private File provideTestFile() {
		final URL resource = this.getClass().getClassLoader().getResource(TEST_FILE_NAME);
		final String fullFilePath = resource.getPath();
		return new File(fullFilePath);
	}
	
	private FeedProcessMessage passwordsChunk(Long processId, int chunkSize) {
		final int delta = 10;
		List<String> chunk = new ArrayList<>(chunkSize + delta);
		for(int i=0; i<chunkSize; i++) {
			chunk.add(PASSWORD_SEED + random.nextLong());
		}
		chunk.add(THE_PASSWORD);
		FeedProcessMessage message = new FeedProcessMessage(chunk, processId);
		return message;
	}

}

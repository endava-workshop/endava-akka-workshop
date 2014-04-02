package akka.ws.pass.breaker.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.ws.pass.breaker.messages.BreakArchiveMessage;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.FeedProcessMessage;
import akka.ws.pass.breaker.messages.FoundPasswordMessage;
import akka.ws.pass.breaker.messages.NewProcessMessage;
import akka.ws.pass.breaker.messages.PasswordChunkMessage;
import akka.ws.pass.breaker.messages.RequestPasswordFlowMessage;
import akka.ws.pass.breaker.messages.StartFeedingProcessMessage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * 
 * @author ddoboga
 *
 */
public class ZipPasswordBreakerTest {

	private ActorSystem actorSystem;
	private static final String TEST_FILE_NAME = "experiment.zip";
	final static File zipArchiveFile = provideTestFile();
	private static final long DELAY_TO_ENSURE_CHILD_RECEIVES_MESSAGES = 500;
	static List<Object> messagesReceivedByWorkDispatcher = new ArrayList<>();
	static List<Object> messagesReceivedByPasswordProvider = new ArrayList<>();
	static ActorRef mockWorkDispatcher;
	static ActorRef mockPasswordProvider;
	private ActorRef zipPasswordBreaker;
	
	@Before
	public void setUp() throws Exception {
		actorSystem = ActorSystem.create("LocalZipBreakerActorSystem");

		mockWorkDispatcher = actorSystem.actorOf(Props.create(MockWorkDispatcher.class));
		mockPasswordProvider = actorSystem.actorOf(Props.create(MockPasswordProvider.class));
		zipPasswordBreaker = actorSystem.actorOf(Props.create(ZipPasswordBreakerExtension.class));
	}

	@After
	public void tearDown() throws Exception {
		File sharedTempFilesFolder = new File(ZipPasswordBreaker.PATH_TO_SHARED_FOLDER);
		assertTrue(sharedTempFilesFolder.exists());
		for(File file : sharedTempFilesFolder.listFiles()) {
			file.delete();
		}
	}

	@Test
	public void testStartFlow() {
		
		new JavaTestKit(actorSystem) {
			{

				BreakArchiveMessage breakArchiveMessage = new BreakArchiveMessage(zipArchiveFile.getAbsolutePath());
				zipPasswordBreaker.tell(breakArchiveMessage, getRef());

				try {
					Thread.sleep(DELAY_TO_ENSURE_CHILD_RECEIVES_MESSAGES);
				} catch (InterruptedException e) {
					fail(e.toString());
				}
				assertEquals(1, messagesReceivedByWorkDispatcher.size());
				assertTrue(messagesReceivedByPasswordProvider.isEmpty());
				NewProcessMessage outMessage = (NewProcessMessage) messagesReceivedByWorkDispatcher.get(0);
				assertTrue(outMessage.getIdProcess() != 0);
				final URL fileUrl = outMessage.getFileURL();
				assertNotNull(fileUrl);
				
				final File fileAvailableAtURL = new File(fileUrl.getPath());
				assertTrue(fileAvailableAtURL.exists());

			}
		};
	}

	@Test
	public void testStartFeedingProcess() {
		
		new JavaTestKit(actorSystem) {
			{
				final long processId = 28734L;

				StartFeedingProcessMessage inMessage = new StartFeedingProcessMessage(processId);

				zipPasswordBreaker.tell(inMessage, getRef());

				try {
					Thread.sleep(DELAY_TO_ENSURE_CHILD_RECEIVES_MESSAGES);
				} catch (InterruptedException e) {
					fail(e.toString());
				}
				
				assertEquals(1, messagesReceivedByPasswordProvider.size());
				assertTrue(messagesReceivedByWorkDispatcher.isEmpty());
				RequestPasswordFlowMessage outMessage = (RequestPasswordFlowMessage) messagesReceivedByPasswordProvider.get(0);
				assertEquals(processId, outMessage.getProcessId());
				
			}
		};
	}

	@Test
	public void testRedirectPasswordChunks() {
		
		new JavaTestKit(actorSystem) {
			{
				final long processId = 28734L;
				@SuppressWarnings("serial")
				final List<String> passwords = new ArrayList<String>() {{
					add("1st password"); add("2nd password"); add("etc.");
				}};

				PasswordChunkMessage inMessage = new PasswordChunkMessage(processId, passwords);

				zipPasswordBreaker.tell(inMessage, getRef());

				try {
					Thread.sleep(DELAY_TO_ENSURE_CHILD_RECEIVES_MESSAGES);
				} catch (InterruptedException e) {
					fail(e.toString());
				}
				
				assertEquals(1, messagesReceivedByWorkDispatcher.size());
				assertTrue(messagesReceivedByPasswordProvider.isEmpty());
				FeedProcessMessage outMessage = (FeedProcessMessage) messagesReceivedByWorkDispatcher.get(0);
				assertEquals(processId, outMessage.getProcessId());
				assertEqualContents(passwords, outMessage.getPasswords());
				
			}
		};
	}

	@Test
	public void testBroadcastFoundPasswordMessage() {
		
		new JavaTestKit(actorSystem) {
			{
				final long processId = 28734L;
				final String successfullPassword = "some password";

				FoundPasswordMessage inMessage = new FoundPasswordMessage(processId, successfullPassword);

				zipPasswordBreaker.tell(inMessage, getRef());

				try {
					Thread.sleep(DELAY_TO_ENSURE_CHILD_RECEIVES_MESSAGES);
				} catch (InterruptedException e) {
					fail(e.toString());
				}

				assertEquals(1, messagesReceivedByWorkDispatcher.size());
				assertEquals(1, messagesReceivedByPasswordProvider.size());

				assertTrue(messagesReceivedByWorkDispatcher.get(0) instanceof EndProcessMessage);
				assertTrue(messagesReceivedByPasswordProvider.get(0) instanceof EndProcessMessage);

				EndProcessMessage outMessage1 = (EndProcessMessage) messagesReceivedByWorkDispatcher.get(0);
				EndProcessMessage outMessage2 = (EndProcessMessage) messagesReceivedByPasswordProvider.get(0);
				
				assertEquals(inMessage.getProcessId(), outMessage1.getProcessId());
				assertEquals(inMessage.getProcessId(), outMessage2.getProcessId());
				
			}
		};
	}
	
	private static File provideTestFile() {
		final URL resource = ZipPasswordBreakerTest.class.getClassLoader().getResource(TEST_FILE_NAME);
		final String fullFilePath = resource.getPath();
		return new File(fullFilePath);
	}
	
	private FeedProcessMessage passwordsChunk(Long processId, String... passwords) {
		FeedProcessMessage message = new FeedProcessMessage(Arrays.asList(passwords), processId);
		return message;
	}
	
	private static void assertEqualContents(Collection<?> collection1, Collection<?> collection2) {
		boolean equalLists = collection1.size() == collection2.size() && collection1.containsAll(collection2);
		if(! equalLists) {
			throw new junit.framework.AssertionFailedError("expected: " + collection1 + " but was: " + collection2);
		}
	}

}


class MockWorkDispatcher extends WorkDispatcher {
	@Override
	public void onReceive(Object message) throws Exception {
		ZipPasswordBreakerTest.messagesReceivedByWorkDispatcher.add(message);
	}
}

class MockPasswordProvider extends PasswordProvider {
	@Override
	public void onReceive(Object message) throws Exception {
		ZipPasswordBreakerTest.messagesReceivedByPasswordProvider.add(message);
	}
}

class ZipPasswordBreakerExtension extends ZipPasswordBreaker {
	@Override
	public void preStart() {
		this.workDispatcher = ZipPasswordBreakerTest.mockWorkDispatcher;
		this.passwordProvider = ZipPasswordBreakerTest.mockPasswordProvider;
	}
}
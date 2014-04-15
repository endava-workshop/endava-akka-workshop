package ro.endava.akka.workshop.app;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.endava.akka.workshop.actors.IndexDispatcherActor;
import ro.endava.akka.workshop.messages.LocalPasswordMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class App {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(App.class);
	
	public static AtomicInteger sentChunks = new AtomicInteger(0);
	public static AtomicInteger indexedChunks = new AtomicInteger(0);

	public static void main(String[] args) {
		ActorSystem akkaSystem = ActorSystem.create("akkaSystem");

		final Props props = Props.create(IndexDispatcherActor.class);
		ActorRef indexDispatcherActor = akkaSystem.actorOf(props);

		LocalPasswordMessage message = new LocalPasswordMessage(
				"res/common_passwords.txt", 1000);

		indexDispatcherActor.tell(message, ActorRef.noSender());

		while (sentChunks.get() == 0 || sentChunks.get() > indexedChunks.get()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LOGGER.debug("sent " + sentChunks.get());
			LOGGER.debug("indexed " + indexedChunks.get());
		}
		
		LOGGER.debug("loaded " + sentChunks.get() + " chunks of passwords");
		
		akkaSystem.shutdown();
	}

}

package ro.endava.akka.workshop.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.endava.akka.workshop.actors.ESAdminActor;
import ro.endava.akka.workshop.actors.IndexDispatcherActor;
import ro.endava.akka.workshop.messages.LocalPasswordMessage;

import java.util.concurrent.atomic.AtomicInteger;

public class App {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(App.class);
	
	public static AtomicInteger sentChunks = new AtomicInteger(0);
	public static AtomicInteger indexedChunks = new AtomicInteger(0);

	public static void main(String[] args) {
        ActorSystem akkaSystem = ActorSystem.create("akkaSystem");


		final Props props = Props.create(IndexDispatcherActor.class);
		ActorRef indexDispatcherActor = akkaSystem.actorOf(props);

        //Cu chunk de 10000 par cele mai bune rezultate; nici cu 1000 nu e rau; cand se ajunge la chunk de 1000000 crapa diect ca e mesajul prea mare
        //Cred ca trebuie configurari de cat din heap sa ii dam elasticului
		LocalPasswordMessage message = new LocalPasswordMessage(
				"/common_passwords_small.txt", 10000);

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

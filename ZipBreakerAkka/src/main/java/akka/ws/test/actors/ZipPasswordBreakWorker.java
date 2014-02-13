package akka.ws.test.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.ws.test.messages.BrokenSuccessMessage;
import akka.ws.test.messages.ZipPasswordBreakWorkerMessage;
import akka.ws.test.service.ZipPasswordBreakService;
import akka.ws.test.service.impl.ZipPasswordBreakServiceImpl;
import net.lingala.zip4j.core.ZipFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * The ZipPasswordBreakWorker class 
 * 
 * @author Daniel DOBOGA
 *
 */
public class ZipPasswordBreakWorker extends UntypedActor {
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	ZipPasswordBreakService zipPasswordBreakService = new ZipPasswordBreakServiceImpl();
	private Map<Long, ZipFile> runningProcesses = new HashMap<Long, ZipFile>();

	public void onReceive(Object message) throws Exception {
		if (message instanceof ZipPasswordBreakWorkerMessage) {
			ZipPasswordBreakWorkerMessage inMessage = (ZipPasswordBreakWorkerMessage) message;
			System.out.println("Worker received message.");

			for (String password : inMessage.getPasswords()) {
				if (zipPasswordBreakService.checkPassword(inMessage.getZipFile(), password)) {
					System.out.println("PASSWORD BROKEN ******************!\nFound password:" + password);
					BrokenSuccessMessage outMessage = new BrokenSuccessMessage(inMessage.getIdProcess(), password);
					getSender().tell(outMessage, getSelf());
				}
				else {
					System.out.println("Wrong password: " + password);
				}
			}

		} else {
			throw new IllegalArgumentException("Unsupported message type: " + message.getClass());
		}
	}
}

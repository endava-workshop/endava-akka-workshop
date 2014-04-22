package akka.ws.pass.breaker.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.ws.pass.breaker.messages.DownloadFinishedMessage;
import akka.ws.pass.breaker.messages.EndProcessMessage;
import akka.ws.pass.breaker.messages.NewProcessMessage;
import akka.ws.pass.breaker.messages.ReadyToProcessMessage;
import akka.ws.pass.breaker.messages.RegisterPasswordCheckersMessage;
import akka.ws.pass.breaker.messages.StartDownloadMessage;
import akka.ws.pass.breaker.messages.StartNewProcessMessage;

/**
 * 
 * The ZipPasswordBreakWorker class 
 * 
 * @author Daniel DOBOGA
 *
 */
public class ZipPasswordBreakWorker extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
//	private static final String WORKER_NUM_KEY = "pass.checker.actor.number";
	
	ActorRef workDispatcher;
	ActorRef zipFileDownloader;
	ActorRef passwordCheckerBroadcastRouter;
	
	public ZipPasswordBreakWorker() {
		initChildren();
	}

	public void onReceive(Object message) throws Exception {
		if(log.isInfoEnabled()) {
			log.info("\n\n************************************\nZipPasswordBreakWorker received " + message + "\n***************************************\n\n");
		}
		
		if(message instanceof NewProcessMessage) {
			workDispatcher = getSender();
			NewProcessMessage inMessage = (NewProcessMessage) message;
			StartDownloadMessage outMessage = new StartDownloadMessage(inMessage.getIdProcess(), inMessage.getFileURL());
			zipFileDownloader.tell(outMessage, getSelf());
			
		} else if(message instanceof DownloadFinishedMessage) {
			DownloadFinishedMessage inMessage = (DownloadFinishedMessage) message;
			StartNewProcessMessage outMessage = new StartNewProcessMessage(inMessage.getProcessId(), inMessage.getZipFile());
			passwordCheckerBroadcastRouter.tell(outMessage, getSelf());
			ReadyToProcessMessage outMessage2 = new ReadyToProcessMessage(inMessage.getProcessId());
			workDispatcher.tell(outMessage2, getSelf());
			
		} else if (message instanceof EndProcessMessage) {
			passwordCheckerBroadcastRouter.tell(message, getSender());

		} else if (message instanceof RegisterPasswordCheckersMessage) {
			RegisterPasswordCheckersMessage inMessage = (RegisterPasswordCheckersMessage) message;
			passwordCheckerBroadcastRouter = inMessage.getPasswordCheckersBroadcastRouter();

		} else {
			throw new IllegalArgumentException("Unsupported message type: " + message.getClass());
		}
	}
	
	private void initChildren() {
		zipFileDownloader = this.getContext().actorOf(Props.create(ZipFileDownloader.class), "zipFileDownloader");
		
//		final int passCheckersNumber = Integer.parseInt(PropertyUtil.getRemoteProperty(WORKER_NUM_KEY));
//		
//		passwordCheckerBroadcastRouter = this.getContext().actorOf(Props.create(PasswordChecker.class).withRouter(new RoundRobinRouter(passCheckersNumber)), "passwordCheckerBroadcastRouter");
		
	}

}

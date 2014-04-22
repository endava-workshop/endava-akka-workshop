package akka.ws.pass.breaker.messages;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * The RegisterPasswordCheckersMessage class represents the message to be sent by the WorkDispatcher to the
 * ZipPasswordBreakWorker communicating the ActorRef of the router able to broadcast messages between the password
 * checkers associated with this ZipPasswordBreakWorker.
 * 
 * @author Daniel DOBOGA
 */
public class RegisterPasswordCheckersMessage implements Serializable {
	private static final long serialVersionUID = -542206199996457975L;

	private ActorRef passwordCheckersBroadcastRouter;

	public RegisterPasswordCheckersMessage(ActorRef broadcastRouter) {
		this.passwordCheckersBroadcastRouter = broadcastRouter;
	}

	public ActorRef getPasswordCheckersBroadcastRouter() {
		return passwordCheckersBroadcastRouter;
	}

}

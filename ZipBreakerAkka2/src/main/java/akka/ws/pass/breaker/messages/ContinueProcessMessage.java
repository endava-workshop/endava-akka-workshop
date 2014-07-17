package akka.ws.pass.breaker.messages;

/**
 * The ContinueProcessMessage class represents the self sent message of the ZipPasswordBreakWorker.
 * This message doesn't need to be serializable, as it's intended to be sent only by the actor to itself.
 * 
 * @author ddoboga
 *
 */
public class ContinueProcessMessage {

}

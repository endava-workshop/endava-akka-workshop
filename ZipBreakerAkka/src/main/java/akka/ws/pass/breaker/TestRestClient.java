package akka.ws.pass.breaker;

import akka.actor.ActorSystem;
import akka.ws.pass.rest.RestClient;

public class TestRestClient {

	final static String LOCAL_ACTOR_SYSTEM_NAME = "LocalZipBreakerActorSystem"; //TODO externalize name in the properties

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		  ActorSystem system = ActorSystem.create(LOCAL_ACTOR_SYSTEM_NAME);
		  
		  RestClient.getPasswords(system, 10, 10);
				  

		system.shutdown();

	}
}

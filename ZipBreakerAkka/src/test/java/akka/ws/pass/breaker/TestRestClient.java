//package akka.ws.pass.breaker;
//
//import java.util.List;
//
//import akka.actor.ActorSystem;
//import akka.ws.pass.rest.RestClient;
//
//
//public class TestRestClient {
//
//	final static String LOCAL_ACTOR_SYSTEM_NAME = "LocalZipBreakerActorSystem"; 
//	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		ActorSystem system = ActorSystem.create(LOCAL_ACTOR_SYSTEM_NAME);
//		
//		List<String> result = RestClient.getPasswords(system, 10, 10);
//		
//		System.out.println(result);
//		system.shutdown();
//
//	}
//}

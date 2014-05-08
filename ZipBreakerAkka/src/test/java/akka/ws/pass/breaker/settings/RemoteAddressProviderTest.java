//package akka.ws.pass.breaker.settings;
//
//import akka.actor.ActorSystem;
//
//import java.util.List;
//
//import org.junit.Test;
//
//import static junit.framework.Assert.assertFalse;
//import static junit.framework.Assert.assertTrue;
//
//public class RemoteAddressProviderTest {
//	
//	final static long DELAY_TO_ENSURE_SHUTDOWN = 1000;
//
//	@Test
//	public void test() throws Exception {
//		List<RemoteAddress> availableAddresses = RemoteAddressProvider.getAvailableRemoteAddresses();
//		assertFalse(availableAddresses.isEmpty());
//		
//		for(RemoteAddress address : availableAddresses) {
//			System.out.println("******************** address *****************");
//			
//			System.out.println("alias: " + address.getAlias());
//			
//			System.out.println("ip: " + address.getIp());
//			assertTrue(isValidIPAddress(address.getIp()));
//			
//			System.out.println("port: " + address.getPort());
//			assertTrue(isValidPortNumber(address.getPort()));
//			
//			System.out.println("protocol: " + address.getProtocol());
//			assertTrue(isValidProtocol(address.getProtocol()));
//			
//			System.out.println("actorSystem: " + address.getActorSystemName());
//			assertTrue(isValidActorSystemName(address.getActorSystemName()));
//		}
//	}
//	
//	private boolean isValidIPAddress(String ip) {
//		if (ip == null) {
//			return false;
//		}
//		else {
//			String[] tokens = ip.split("\\.");
//			if (tokens.length != 4) {
//				return false;
//			}
//			for (String str : tokens) {
//				int i = Integer.parseInt(str);
//				if ((i < 0) || (i > 255)) {
//					return false;
//				}
//			}
//			return true;
//		}
//	}
//	
//	private boolean isValidPortNumber(int portNo) {
//		return portNo > 0 && portNo < 65536;
//	}
//	
//	private boolean isValidProtocol(String protocol) {
//		if(protocol == null) {
//			return false;
//		}
//		
//		//for now allow tcp only (and case sensitive).
//		switch(protocol) {
//			case "akka.tcp" :
//				return true;
//			default :
//				return false;
//		}
//	}
//	
//	private boolean isValidActorSystemName(String name) throws Exception {
//		if(name == null || name.isEmpty()) {
//			return false;
//		}
//		ActorSystem actorSystem = null;
//		try {
//			 actorSystem = ActorSystem.create(name);
//			 return true;
//		} catch(Exception e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			if(actorSystem != null) {
//				actorSystem.shutdown();
//				 Thread.sleep(DELAY_TO_ENSURE_SHUTDOWN);
//			}
//		}
//	}
//
//}

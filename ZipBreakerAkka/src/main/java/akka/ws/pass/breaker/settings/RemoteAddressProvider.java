package akka.ws.pass.breaker.settings;

import akka.ws.pass.breaker.LocalApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class RemoteAddressProvider {

	public static final String ALIAS_KEY = "alias";
	public static final String IP_KEY = "ip";
	public static final String PORT_KEY = "port";
	public static final String PROTOCOL_KEY = "protocol";
	public static final String ACTOR_SYSTEM_NAME_KEY = "actorSystem";
	public static final String REMOTE_ADDRESSES_ARRAY_KEY = "remoteAddresses";
	public static final String REMOTE_ADDRESSES_FILE_NAME = "remoteAddresses.json";
	

	public static void main(String[] args) throws Exception {
		
		List<RemoteAddress> availableAddresses = getAvailableRemoteAddresses();
		for(RemoteAddress address : availableAddresses) {
			System.out.println("******************** address *****************");
			System.out.println("alias: " + address.getAlias());
			System.out.println("ip: " + address.getIp());
			System.out.println("port: " + address.getPort());
			System.out.println("protocol: " + address.getProtocol());
			System.out.println("actorSystem: " + address.getActorSystemName());
		}
	}
	
	public static List<RemoteAddress> getAvailableRemoteAddresses() {

		String source = null;
		try {
			source = readFullStreamAndClose(LocalApplication.class.getClassLoader().getResourceAsStream(REMOTE_ADDRESSES_FILE_NAME));
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		List<RemoteAddress> addressList = new ArrayList<RemoteAddress>();
		
		JSONObject remoteAddressesJSON = new JSONObject(source);
		JSONArray addressesArray = remoteAddressesJSON.getJSONArray("remoteAddresses");
		for (int i = 0; i < addressesArray.length(); i++) {
			JSONObject addressJSON = addressesArray.getJSONObject(i);
			RemoteAddress remoteAddress = RemoteAddress.newBuilder()
					.withAlias(addressJSON.getString(ALIAS_KEY))
					.withIP(addressJSON.getString(IP_KEY))
					.withPort(addressJSON.getInt(PORT_KEY))
					.withProtocol(addressJSON.getString(PROTOCOL_KEY))
					.withActorSystemName(addressJSON.getString(ACTOR_SYSTEM_NAME_KEY))
					.build();
			addressList.add(remoteAddress);
		}
		
		return addressList;
	}

	private static String readFullStreamAndClose(InputStream in) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			return out.toString();
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
}

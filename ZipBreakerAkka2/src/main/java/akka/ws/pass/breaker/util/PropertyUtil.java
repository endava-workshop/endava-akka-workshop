package akka.ws.pass.breaker.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {
	private static final String PROPERTIES_FILE_NAME = "remote.properties";

	public static String getRemoteProperty(String key) {
		Properties props = new Properties();
		try {
			props.load(PropertyUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME));
		}
		catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return (String) props.get(key);
	}
}

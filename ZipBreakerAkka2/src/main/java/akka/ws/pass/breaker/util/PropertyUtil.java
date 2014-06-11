package akka.ws.pass.breaker.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {
	private static final String PROPERTIES_FILE_NAME = "settings.properties";
	private static final Properties properties;
	
	static {
		properties = new Properties();
		try {
			properties.load(PropertyUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME));
		}
		catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String getStringProperty(String key) {
		return (String) properties.get(key);
	}

	public static Object getProperty(String key) {
		return properties.get(key);
	}

	public static boolean getBooleanProperty(String key) {
		return Boolean.parseBoolean((String) properties.get(key));
	}

	public static int getIntProperty(String key) {
		return Integer.parseInt((String) properties.get(key));
	}

}

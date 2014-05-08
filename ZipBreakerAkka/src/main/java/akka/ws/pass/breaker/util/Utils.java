package akka.ws.pass.breaker.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {

	/**
	 * Get the full stack trace of a throwable, including cause, information in superclass and suppressed throwables if any.
	 * @param throwable a Throwable
	 * @return a String representation of the full stack trace extracted from the given Throwable
	 */
	public static String getFullStackTrace(Throwable throwable) {
		try(StringWriter writer = new StringWriter()) {
			throwable.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		} catch(IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}

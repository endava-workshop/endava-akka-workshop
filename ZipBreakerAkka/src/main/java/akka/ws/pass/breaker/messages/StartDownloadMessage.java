package akka.ws.pass.breaker.messages;

import java.net.URL;

/**
 * 
 * The StartDownloadMessage class 
 * 
 * @author ddoboga
 *
 */
public class StartDownloadMessage extends BaseProcessMessage {

	private static final long serialVersionUID = 3066622975501234212L;

	private URL url;

	public StartDownloadMessage(long processId, URL url) {
		super(processId);
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}

}

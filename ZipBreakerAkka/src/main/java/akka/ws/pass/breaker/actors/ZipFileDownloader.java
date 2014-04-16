package akka.ws.pass.breaker.actors;

import akka.event.Logging;
import akka.event.LoggingAdapter;

import akka.actor.UntypedActor;
import akka.ws.pass.breaker.messages.DownloadFinishedMessage;
import akka.ws.pass.breaker.messages.StartDownloadMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

/**
 * The ZipFileDownloader class is intended to download zip file associated to one process, from the local machine to the
 * remote machine.
 * 
 * @author Daniel DOBOGA
 */
public class ZipFileDownloader extends UntypedActor {
	private static final String ZIP_EXTENSION = "zip";
	private static final String TEMP_FOLDER = "d:/tmp"; //TODO externalize in properties
	
	private Random random = new Random();
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public void onReceive(Object message) throws Exception {
		if(log.isInfoEnabled()) {
			log.info("\n\n*******************************\nZipFileDownloader received " + message + "\n*************************************\n\n");
		}

		if (message instanceof StartDownloadMessage) {

			StartDownloadMessage inMessage = (StartDownloadMessage) message;
			final long processId = inMessage.getProcessId();
			final String destinationPath = createFilePathNameFor(inMessage);
			
			try (
					InputStream inputStream = inMessage.getUrl().openStream();
					FileOutputStream fos = new FileOutputStream(destinationPath);
					) {
				byte[] buf = new byte[2048];
				int byteRead = 0;
				int byteWritten = 0;
				while((byteRead = inputStream.read(buf)) != -1) {
					fos.write(buf, 0, byteRead);
					byteWritten += byteRead;
				}
				
				getSender().tell(new DownloadFinishedMessage(processId, new File(destinationPath)), getSelf());
			}

		}
	}
	
	String createFilePathNameFor(StartDownloadMessage message) {
		return TEMP_FOLDER + "/" + message.getProcessId() + "_" + random.nextInt() + "." + ZIP_EXTENSION;
	}

}

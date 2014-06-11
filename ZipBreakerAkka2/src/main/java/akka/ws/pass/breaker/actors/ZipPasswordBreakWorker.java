package akka.ws.pass.breaker.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.ws.pass.breaker.exception.PasswordsExhaustedException;
import akka.ws.pass.breaker.messages.FoundPasswordMessage;
import akka.ws.pass.breaker.messages.StartWorkerMessage;
import akka.ws.pass.breaker.util.LocalPasswordsProvider;
import akka.ws.pass.rest.RestClient;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * 
 * The ZipPasswordBreakWorker class 
 * 
 * @author Daniel DOBOGA
 *
 */
public class ZipPasswordBreakWorker extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private static final long MAX_POSSIBLE_CHUNKS = Long.MAX_VALUE;
	

	private static final String ZIP_EXTENSION = "zip";
	private static final String TEMP_FOLDER = "d:/tmp"; //TODO externalize in properties
	private static final boolean PASSWORDS_FROM_FILE = true; //TODO externalize in properties
	
	private int workerIndex;
	private int totalWorkers;
	private Long processId;
	private URL sharedFileUrl;
	private ZipFile zipFile;
	private String tempOutputFilePath;
	private int passwordChunkSize;

	public void onReceive(Object message) throws Exception {
		if(log.isInfoEnabled()) {
			log.info("\n\n******** ZipPasswordBreakWorker received " + message + "\n***************************\n\n");
		}
		
		if(message instanceof StartWorkerMessage) {
			StartWorkerMessage inMessage = (StartWorkerMessage) message;
			workerIndex = inMessage.getWorkerIndex();
			totalWorkers = inMessage.getTotalWorkers();
			processId = inMessage.getIdProcess();
			sharedFileUrl = inMessage.getFileURL();
			passwordChunkSize = inMessage.getPasswordChunkSize();
			
			makeFileAvailableLocally();
			establishTempOutputFilePath();
			
			logWorkerStateFollowingInit();
			
			for(int chunkIndex=0; chunkIndex<MAX_POSSIBLE_CHUNKS; chunkIndex++) {
				if(chunkIndex % totalWorkers == workerIndex) {
					processPasswordChunkWithIndex(chunkIndex);
				}
			}
		} else {
			throw new IllegalArgumentException("Unsupported message type: " + message.getClass());
		}
	}
	
	private List<String> loadPasswordChunk(final int chunkIndex, final int chunkSize) {
		final int fromIndex = chunkIndex * chunkSize;
		if(PASSWORDS_FROM_FILE) {
			return LocalPasswordsProvider.getPasswords(fromIndex, chunkSize);
		} else {
			return RestClient.getPasswords(getContext().system(), fromIndex, chunkSize);
		}
	}
	
	private void processPasswordChunkWithIndex(final int chunkIndex) throws PasswordsExhaustedException {
//		log.info("\n\n******** load passwords chunk with index " + chunkIndex + "\n\n");
		List<String> passwordChunk = loadPasswordChunk(chunkIndex, passwordChunkSize);
//		log.info("\n\n******** loaded passwords chunk. First password in chunk: " + passwordChunk.get(0) + "\n\n");
		for(String password : passwordChunk) {
			processOnePassword(password);
		}
		logProcessedChunkInformation(passwordChunk, chunkIndex);
		if(passwordChunk.size() < passwordChunkSize) {
			throw new PasswordsExhaustedException();
		}
	}
	
	private void processOnePassword(final String password) {
		if (checkPassword(zipFile, password)) {
			log.info("\nPASSWORD BROKEN ******************!\nFound password:" + password);
			FoundPasswordMessage outMessage = new FoundPasswordMessage(processId, password);
			getSender().tell(outMessage, getSelf());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean checkPassword(ZipFile zipFile, String password) {
		try {
			zipFile.setPassword(password);
			zipFile.extractAll(tempOutputFilePath);
			return true;
		}
		catch (ZipException e) {
			return false;
		}
		finally {
			File destinationFolder = new File(tempOutputFilePath);
			if (destinationFolder.exists()) {
				delete(destinationFolder);
			}
		}
	}

	/**
	 * Deletes the given file. If the file is directory, it recursively deletes.
	 * @param theFile
	 */
	private static void delete(File theFile) {
		if (theFile.isDirectory()) {
			File[] children = theFile.listFiles();
			if(children != null) {
				for (File child : children) {
					if(child != null && child.exists()) {
						delete(child);
					}
				}
			}
		}
		theFile.delete();
	}

	private void deleteLocalFileCopy() {
		File file = zipFile.getFile();
		if(file.exists()) {
			file.delete();
		}
	}

	private void logProcessedChunkInformation(List<String> processedChunk, int chunkIndex) {
		if(log.isInfoEnabled()) {

			StringBuilder message = new StringBuilder();
			message.append("\n****************************** Checked chunk number ")
			.append(chunkIndex)
//			.append(" ***************************\n")
//			.append("first password in chunk: ").append(processedChunk.get(0))
//			.append("last password in chunk: ").append(processedChunk.get(processedChunk.size() - 1))
			.append("\n\n");
			
			log.info(message.toString());
		}
	}

	private void logWorkerStateFollowingInit() {
		if(log.isInfoEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("\n****************************** ZipPasswordBreakWorker initialized: ")
			.append("processId=").append(processId).append('\n')
			.append("workerIndex=").append(workerIndex).append('\n')
			.append("totalWorkers=").append(totalWorkers).append('\n')
			.append("sharedFileUrl=").append(sharedFileUrl).append('\n')
			.append("zipFile=").append(zipFile).append('\n')
			.append("tempOutputFilePath=").append(tempOutputFilePath).append('\n')
			.append("passwordChunkSize=").append(passwordChunkSize).append('\n')
			.append("\n***********************************************\n");
			
			log.info(message.toString());
		}
	}

	private void makeFileAvailableLocally() throws IOException, ZipException {
		final StringBuilder sb = new StringBuilder(TEMP_FOLDER).append('/').append(processId).append('_').append(workerIndex).append('.').append(ZIP_EXTENSION);
		final String destinationPath = sb.toString();
		
		try (
				InputStream inputStream = this.sharedFileUrl.openStream();
				FileOutputStream fos = new FileOutputStream(destinationPath);
				) {
			byte[] buf = new byte[2048];
			int byteRead = 0;
			int byteWritten = 0;
			while((byteRead = inputStream.read(buf)) != -1) {
				fos.write(buf, 0, byteRead);
				byteWritten += byteRead;
			}
			
			this.zipFile = new ZipFile(new File(destinationPath));
		}
	}
	
	private void establishTempOutputFilePath() {
		StringBuilder sb = new StringBuilder(TEMP_FOLDER).append('/').append(processId).append('_').append(workerIndex).append('_').append("output").append('.').append(ZIP_EXTENSION);
		tempOutputFilePath = sb.toString();
	}

	@Override
	public void postStop() {
		deleteLocalFileCopy();
		
		File destinationFolder = new File(tempOutputFilePath);
		if (destinationFolder.exists()) {
			delete(destinationFolder);
		}
	}
	
}

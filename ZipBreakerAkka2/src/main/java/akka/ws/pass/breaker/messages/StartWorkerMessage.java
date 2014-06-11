package akka.ws.pass.breaker.messages;

import java.io.Serializable;
import java.net.URL;

/**
 * @author Daniel DOBOGA
 */
public class StartWorkerMessage implements Serializable {

	private static final long serialVersionUID = -3678664521646822376L;

	private long idProcess;
	
	private int totalWorkers;

	private int workerIndex;

	private int passwordChunkSize;

	private URL fileURL;

	public StartWorkerMessage(long idProcess, URL fileUrl, int workerIndex, int passwordChunkSize, int totalWorkers) {
		super();
		this.idProcess = idProcess;
		this.fileURL = fileUrl;
		this.workerIndex = workerIndex;
		this.passwordChunkSize = passwordChunkSize;
		this.totalWorkers = totalWorkers;
	}

	public long getIdProcess() {
		return idProcess;
	}

	public URL getFileURL() {
		return fileURL;
	}

	public int getWorkerIndex() {
		return workerIndex;
	}

	public int getPasswordChunkSize() {
		return passwordChunkSize;
	}

	public int getTotalWorkers() {
		return totalWorkers;
	}

}

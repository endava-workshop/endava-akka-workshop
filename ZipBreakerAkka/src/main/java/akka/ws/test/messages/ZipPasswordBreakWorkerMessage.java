package akka.ws.test.messages;

import net.lingala.zip4j.core.ZipFile;

import java.util.List;

public class ZipPasswordBreakWorkerMessage {

	private List<String> passwords;

	private long idProcess;

	private ZipFile zipFile;

	public ZipPasswordBreakWorkerMessage(List<String> passwords, long idProcess, ZipFile zipFile) {
		super();
		this.passwords = passwords;
		this.idProcess = idProcess;
		this.zipFile = zipFile;
	}

	public List<String> getPasswords() {
		return passwords;
	}

	public long getIdProcess() {
		return idProcess;
	}

	public ZipFile getZipFile() {
		return zipFile;
	}

}

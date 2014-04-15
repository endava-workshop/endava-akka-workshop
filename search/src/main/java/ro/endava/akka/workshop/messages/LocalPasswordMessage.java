package ro.endava.akka.workshop.messages;

import java.io.Serializable;

public class LocalPasswordMessage implements Serializable {

	private int bulkSize;

	private String resourceFilePath;

	public LocalPasswordMessage(String resourceFilePath, int bulkSize) {
		if (bulkSize <= 0) {
			throw new IllegalArgumentException(
					"bulk size must be greater than zero and not " + bulkSize);
		}
		if (resourceFilePath == null || resourceFilePath.length() == 0) {
			throw new IllegalArgumentException(
					"resourceFilePath cannot be null or empty string ");
		}
		this.bulkSize = bulkSize;
		this.resourceFilePath = resourceFilePath;
	}

	public int getBulkSize() {
		return bulkSize;
	}

	public String getResourceFilePath() {
		return resourceFilePath;
	}

}

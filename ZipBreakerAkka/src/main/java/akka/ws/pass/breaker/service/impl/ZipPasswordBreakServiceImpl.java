package akka.ws.pass.breaker.service.impl;

import akka.ws.pass.breaker.service.ZipPasswordBreakService;

import net.lingala.zip4j.exception.ZipException;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;

public class ZipPasswordBreakServiceImpl implements ZipPasswordBreakService {

	private static final AtomicSequence sequence = new AtomicSequence();

	/**
	 * {@inheritDoc}
	 */
	public boolean checkPassword(ZipFile zipFile, String password) {
		String destination = zipFile.getFile().getParent() + File.separatorChar + sequence.next();
		try {
			zipFile.setPassword(password);
			zipFile.extractAll(destination);
			return true;
		}
		catch (ZipException e) {
			return false;
		}
		finally {
			File destinationFolder = new File(destination);
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
			for (File child : theFile.listFiles()) {
				delete(child);
			}

		}
		theFile.delete();
	}

	/**
	 * The AtomicSequence class is intended to offer String values that are unique within a small horizon of time.
	 * 
	 * @author ddoboga
	 */
	private static class AtomicSequence {
		private static final int DELTA = 10;

		private static final int SEQUENCE_LIMIT = Integer.MAX_VALUE - DELTA;

		private static final String SEED = "tmpunzprz_";

		private int sequence = 0;

		/**
		 * @return a String that is guaranteed to be different from the ones generated recently. It is possible that
		 *         this method return a sequence that was returned long ago, but not within the past few minutes. This
		 *         is intended to offer unique identifiers to differentiate processes that occur nearly at the same time
		 *         and live a short period of time (typically only few nanoseconds).
		 */
		public synchronized String next() {
			if (sequence >= SEQUENCE_LIMIT) {
				sequence = 0;
			}
			return SEED + ++sequence;
		}
	}

}

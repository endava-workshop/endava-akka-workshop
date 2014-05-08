package akka.ws.pass.breaker;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;

public class ForkJoinPassBreakerApp {

	public final static String PASSWORD_FILE_NAME = "common_passwords.txt";
	final static String zipArchivePath = "D:/workspace/EndavaAkkaWs/ZipBreakerAkka/src/main/resources/experiment_outer.zip";
	static long startTime;
	static AtomicInteger totalCheckedPasswords = new AtomicInteger(0);

	public static void main(String[] args) throws Exception {
		startTime = System.currentTimeMillis();

		final ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
		
		final URL passwordFileURL = ForkJoinPassBreakerApp.class.getClassLoader().getResource(PASSWORD_FILE_NAME);
		final File passwordFile = new File(passwordFileURL.getPath());
		pool.invoke(new PassCheckerAction(zipArchivePath, passwordFileURL, 0, passwordFile.length()));
		

		System.out.println("Number of steals: " + pool.getStealCount());
	}
}




class PassCheckerAction extends RecursiveAction {
	private static final long serialVersionUID = -1680519863476352614L;
	private static final long SPLIT_THRESHOLD = 20000;
	private static final AtomicSequence sequence = new AtomicSequence();
	
	URL passFileURL;
	String zipFilePath;
	long low;
	long high;
	
	public PassCheckerAction(String filePath, URL passFileURL, long low, long high) {
		this.zipFilePath = filePath;
		this.passFileURL = passFileURL;
		this.low = low;
		this.high = high;
	}
	
	@Override
	protected void compute() {
		if(high - low > SPLIT_THRESHOLD) {
			// task is large enough to be divided in half
			long mid = (low + high) >>> 1;
			invokeAll(asList(new PassCheckerAction(zipFilePath, passFileURL, low, mid), new PassCheckerAction(zipFilePath, passFileURL, mid, high)));
		} else {
			computeSequential();
		}
	}
	
	private void computeSequential() {
		try(RandomAccessFile raf = new RandomAccessFile(passFileURL.getPath(), "r")) {
			raf.seek(low);
			ZipFile zipFile = new ZipFile(zipFilePath);
			do {
				String password = raf.readLine();
				if(checkPassword(zipFile, password)) {
					System.out.println("*************** Found password: " + password);
					System.out.println("Total time millis: " + (System.currentTimeMillis() - ForkJoinPassBreakerApp.startTime));
					System.exit(0);
				}
				final int totalPasswords = ForkJoinPassBreakerApp.totalCheckedPasswords.incrementAndGet();
				if(totalPasswords % 10000 == 0) {
					System.out.println(totalPasswords + " checked in " + (System.currentTimeMillis() - ForkJoinPassBreakerApp.startTime));
				}
			} while(raf.getFilePointer() < high);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean checkPassword(ZipFile zipFile, String password) {
		if(password == null || "".equals(password)) {
			return false;
		}
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
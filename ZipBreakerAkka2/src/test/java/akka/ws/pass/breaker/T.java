package akka.ws.pass.breaker;

import net.lingala.zip4j.util.Zip4jUtil;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/*
 * Timpi obtinuti doar pentru generare de parole:
 * 2 chars      31
 * 3 chars     490
 * 4 chars   22000
 * 5 chars 1439889
 */
public class T {
	final static String acceptedPasswordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_- ";
	final static int acceptedChars = acceptedPasswordChars.length();
	public static volatile String result;
	final static String zipArchivePath = "D:/backup/akka.zip";

	public static void main(String[] args) throws Exception {

		final long start = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		
		System.out.println(attack(new ZipFile(zipArchivePath)));
		
		final long end = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		System.out.println("Total time: " + TimeUnit.NANOSECONDS.toMillis(end - start));

	}
	
	private static String attack(ZipFile zipFile) {
		ZipPasswordChecker checker = new ZipPasswordChecker();
		checker.setZipFile(zipFile);
		String foundPassword = null;
		if(foundPassword == null)
			foundPassword = tokensOfSize1(checker);
		if(foundPassword == null)
			foundPassword = tokensOfSize2(checker);
		if(foundPassword == null)
			foundPassword = tokensOfSize3(checker);
		if(foundPassword == null)
			foundPassword = tokensOfSize4(checker);
		if(foundPassword == null)
			foundPassword = tokensOfSize5(checker);
		if(foundPassword == null)
			foundPassword = tokensOfSize6(checker);
		return foundPassword;
	}
	
	private static interface PasswordChecker {
		boolean checkPassword(String password);
	}

	private static String tokensOfSize1(PasswordChecker processor) {
		final int passwordLength = 1;
		for (int index1 = 0; index1 < acceptedChars; index1++) {
			StringBuilder token = new StringBuilder(passwordLength).append(acceptedPasswordChars.charAt(index1));
			if(processor.checkPassword(token.toString())) {
				return token.toString();
			}
		}
		return null;
	}

	private static String tokensOfSize2(PasswordChecker processor) {
		final int passwordLength = 2;
		for (int index1 = 0; index1 < acceptedChars; index1++) {
			for (int index2 = 0; index2 < acceptedChars; index2++) {
				StringBuilder token = new StringBuilder(passwordLength).append(
						acceptedPasswordChars.charAt(index1)).append(
						acceptedPasswordChars.charAt(index2));

				if(processor.checkPassword(token.toString())) {
					return token.toString();
				}
			}
		}
		return null;
	}

	private static String tokensOfSize3(PasswordChecker processor) {
		final int passwordLength = 3;
		for (int index1 = 0; index1 < acceptedChars; index1++) {
			for (int index2 = 0; index2 < acceptedChars; index2++) {
				for (int index3 = 0; index3 < acceptedChars; index3++) {
					StringBuilder token = new StringBuilder(passwordLength)
							.append(acceptedPasswordChars.charAt(index1))
							.append(acceptedPasswordChars.charAt(index2))
							.append(acceptedPasswordChars.charAt(index3));
					if(processor.checkPassword(token.toString())) {
						return token.toString();
					}
				}
			}
		}
		return null;
	}

	private static String tokensOfSize4(PasswordChecker processor) {
		final int passwordLength = 4;
		for (int index1 = 0; index1 < acceptedChars; index1++) {
			for (int index2 = 0; index2 < acceptedChars; index2++) {
				for (int index3 = 0; index3 < acceptedChars; index3++) {
					for (int index4 = 0; index4 < acceptedChars; index4++) {
						StringBuilder token = new StringBuilder(passwordLength)
								.append(acceptedPasswordChars.charAt(index1))
								.append(acceptedPasswordChars.charAt(index2))
								.append(acceptedPasswordChars.charAt(index3))
								.append(acceptedPasswordChars.charAt(index4));
						if(processor.checkPassword(token.toString())) {
							return token.toString();
						}
					}
				}
			}
		}
		return null;
	}

	private static String tokensOfSize5(PasswordChecker processor) {
		final int passwordLength = 5;
		for (int index1 = 0; index1 < acceptedChars; index1++) {
			for (int index2 = 0; index2 < acceptedChars; index2++) {
				for (int index3 = 0; index3 < acceptedChars; index3++) {
					for (int index4 = 0; index4 < acceptedChars; index4++) {
						for (int index5 = 0; index5 < acceptedChars; index5++) {
							StringBuilder token = new StringBuilder(passwordLength)
									.append(acceptedPasswordChars.charAt(index1))
									.append(acceptedPasswordChars.charAt(index2))
									.append(acceptedPasswordChars.charAt(index3))
									.append(acceptedPasswordChars.charAt(index4))
									.append(acceptedPasswordChars.charAt(index5));
							if(processor.checkPassword(token.toString())) {
								return token.toString();
							}
						}
					}
				}
			}
		}
		return null;
	}

	private static String tokensOfSize6(PasswordChecker processor) {
		final int passwordLength = 6;
		for (int index1 = 0; index1 < acceptedChars; index1++) {
			for (int index2 = 0; index2 < acceptedChars; index2++) {
				for (int index3 = 0; index3 < acceptedChars; index3++) {
					for (int index4 = 0; index4 < acceptedChars; index4++) {
						for (int index5 = 0; index5 < acceptedChars; index5++) {
							for (int index6 = 0; index6 < acceptedChars; index6++) {
								StringBuilder token = new StringBuilder(passwordLength)
										.append(acceptedPasswordChars.charAt(index1))
										.append(acceptedPasswordChars.charAt(index2))
										.append(acceptedPasswordChars.charAt(index3))
										.append(acceptedPasswordChars.charAt(index4))
										.append(acceptedPasswordChars.charAt(index5))
										.append(acceptedPasswordChars.charAt(index6));
								if(processor.checkPassword(token.toString())) {
									return token.toString();
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private static String tokensOfSize7(PasswordChecker processor) {
		final int passwordLength = 7;
		for (int index1 = 0; index1 < acceptedChars; index1++) {
			for (int index2 = 0; index2 < acceptedChars; index2++) {
				for (int index3 = 0; index3 < acceptedChars; index3++) {
					for (int index4 = 0; index4 < acceptedChars; index4++) {
						for (int index5 = 0; index5 < acceptedChars; index5++) {
							for (int index6 = 0; index6 < acceptedChars; index6++) {
								for (int index7 = 0; index7 < acceptedChars; index7++) {
									StringBuilder token = new StringBuilder(passwordLength)
											.append(acceptedPasswordChars.charAt(index1))
											.append(acceptedPasswordChars.charAt(index2))
											.append(acceptedPasswordChars.charAt(index3))
											.append(acceptedPasswordChars.charAt(index4))
											.append(acceptedPasswordChars.charAt(index5))
											.append(acceptedPasswordChars.charAt(index6))
											.append(acceptedPasswordChars.charAt(index7));
									if(processor.checkPassword(token.toString())) {
										return token.toString();
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private static String tokensOfSize8(PasswordChecker processor) {
		final int passwordLength = 8;
		for (int index1 = 0; index1 < acceptedChars; index1++) {
			for (int index2 = 0; index2 < acceptedChars; index2++) {
				for (int index3 = 0; index3 < acceptedChars; index3++) {
					for (int index4 = 0; index4 < acceptedChars; index4++) {
						for (int index5 = 0; index5 < acceptedChars; index5++) {
							for (int index6 = 0; index6 < acceptedChars; index6++) {
								for (int index7 = 0; index7 < acceptedChars; index7++) {
									for (int index8 = 0; index8 < acceptedChars; index8++) {
										StringBuilder token = new StringBuilder(passwordLength)
												.append(acceptedPasswordChars.charAt(index1))
												.append(acceptedPasswordChars.charAt(index2))
												.append(acceptedPasswordChars.charAt(index3))
												.append(acceptedPasswordChars.charAt(index4))
												.append(acceptedPasswordChars.charAt(index5))
												.append(acceptedPasswordChars.charAt(index6))
												.append(acceptedPasswordChars.charAt(index7))
												.append(acceptedPasswordChars.charAt(index8));
										if(processor.checkPassword(token.toString())) {
											return token.toString();
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}



private static class ZipPasswordChecker implements PasswordChecker {
	private static final AtomicSequence sequence = new AtomicSequence();
	private ZipFile zipFile;
	
	public void setZipFile(ZipFile zipFile) {
		this.zipFile = zipFile;
	}
	
	public static boolean checkPassword(ZipFile zipFile, String password) {
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

	@Override
	public boolean checkPassword(String password) {
		if (!Zip4jUtil.isStringNotNullAndNotEmpty(password)) {
			return false;
		}
		return checkPassword(zipFile, password);
	}
}
	
}
package akka.ws.pass.breaker.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LocalPasswordsProvider {

	private final static String PASSWORD_FILE_PATH = "D:/workspace/EndavaAkkaWs/ZipBreakerAkka2/src/main/resources/common_passwords.txt";
	private final static List<String> result = new ArrayList<>(4152000);
	private final static int resultSize;

	static {
		try (RandomAccessFile raf = new RandomAccessFile(new File(PASSWORD_FILE_PATH), "r")) {
			final long fileLength = raf.length();
			do {
				result.add(raf.readLine());
			}
			while (raf.getFilePointer() < fileLength);
			
			resultSize = result.size();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<String> getPasswords(int fromIndex, final int chunkSize) {
		List<String> list = new ArrayList<>(chunkSize);
		int added = 0;
		for(int i=fromIndex; i<resultSize && added<chunkSize; i++) {
			list.add(result.get(i));
			added ++;
		}
		return list;
	}
	
	public static void main(String[] args) {
		List<String> passwordChunk = getPasswords(35000, 5000);
	}
}

package akka.ws.pass.breaker.service;

import net.lingala.zip4j.core.ZipFile;

public interface ZipPasswordBreakService {

	/**
	 * Attempts to extract the contents of the given ZipFile using the given password. This can be used for brute-force
	 * attacks.
	 * 
	 * @param zipFile
	 * @param password
	 * @return true if the password matched; false otherwise.
	 */
	public boolean checkPassword(ZipFile zipFile, String password);

}

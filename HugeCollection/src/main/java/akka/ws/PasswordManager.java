package akka.ws;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

public class PasswordManager {

	private HsqlManager dbManager;
	
	public void loadPasswords(String fileLocation) {
		if(dbManager == null){
			dbManager = new HsqlManager();
		}
		populateDatabase(fileLocation);
	}
	
	private void populateDatabase(String fileLocation){
		try {
			FileInputStream fstream = new FileInputStream(fileLocation);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String password;
			Long index = 0L;
			while ((password = br.readLine()) != null) {
				dbManager.addPassword(password);
				index++;
				if(index % 100000 == 0){
					System.out.println("added " + index + " passwords so far");
				}
			}
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public List<String> getPasswords(Integer pageIndex, Integer pageSize){
		try {
			return dbManager.getPasswords(pageIndex, pageSize);
		} catch (Exception e) {
			return null;
		}
	}
	
	public void closeServer(){
		dbManager.stopServer();
	}

	public int getPasswordNumber() {
		try {
			return dbManager.getPasswordNumber();
		} catch (Exception e) {
			return 0;
		}
	}
}

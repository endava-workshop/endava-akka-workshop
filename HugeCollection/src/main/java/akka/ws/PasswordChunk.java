package akka.ws;

import java.util.ArrayList;
import java.util.List;

public class PasswordChunk implements PasswordChunkInterface{

	private Integer chunkSize;
	private List<String> passwordList;
	
	public PasswordChunk(Integer chunkSize){
		this.chunkSize = chunkSize;
	}
	
	public void addPassword(String password) throws Exception{
		if(passwordList == null){
			passwordList = new ArrayList<String>();
		}
		if(passwordList.size() < chunkSize){
			passwordList.add(password);
		} else {
			throw new Exception("list full, unable to add more passwords");
		}
	}

	public String getPassword(Integer index){
		if(passwordList == null){
			return null;
		}
		if(index < 0 || index > passwordList.size() - 1){
			return null;
		}
		return passwordList.get(index);
	}
	/**
	 * @return the chunkSize
	 */
	public Integer getChunkCurrSize() {
		return passwordList != null ? passwordList.size() : 0;
	}
	
	/**
	 * @return the chunkSize
	 */
	public Integer getChunkMaxSize() {
		return chunkSize;
	}
	
	
}

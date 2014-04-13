package akka.ws;

public interface PasswordChunkInterface {

	public void addPassword(String password) throws Exception;
	
	public String getPassword(Integer index);
	
	public Integer getChunkCurrSize();
	
	public Integer getChunkMaxSize();
}

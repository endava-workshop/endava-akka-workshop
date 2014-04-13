package akka.ws;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	long time = System.currentTimeMillis();
        PasswordManager manager = new PasswordManager();
        manager.loadPasswords("res/common_passwords.txt");
        System.out.println("time to load all passwords : " + (System.currentTimeMillis() - time));

        int totalPass = manager.getPasswordNumber();
        System.out.println("loaded all passwords : " + totalPass);

        List<String> passwords = manager.getPasswords(7, 10);
        
        System.out.println(passwords.size());
        for(String pass : passwords){
        	System.out.println(pass);
        }
        manager.closeServer();
    }
}

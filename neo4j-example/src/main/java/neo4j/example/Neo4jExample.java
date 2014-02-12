package neo4j.example;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Hello world!
 *
 */
public class Neo4jExample 
{
    public static void main( String[] args )
    {
        Neo4jService neo4jService = new Neo4jService();
        
        if(!neo4jService.checkDatabaseIsRunning()){
        	System.out.println("error initialising neo4j; check database connection and neo4j server status");
        }
        
        URI rootNode = neo4jService.populateDatabase();
        
        try {
			String response = neo4jService.findLinksForNode(rootNode);
			
			System.out.println("nodes realted to " + rootNode.toString() + " : ");
			System.out.println(response);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}

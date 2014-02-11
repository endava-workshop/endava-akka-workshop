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
        
        URI nodeWikipedia = neo4jService.createNode("Wikipedia", "http://www.wikipedia.com");
        
        URI nodeYahoo = neo4jService.createNode("Yahoo", "http://www.yahoo.com");
        
        try {
			neo4jService.addRelationship(nodeWikipedia, nodeYahoo, "links to", "{ \"how manny times\" : \"123\" }");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    }
    
}

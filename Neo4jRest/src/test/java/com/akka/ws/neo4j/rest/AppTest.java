package com.akka.ws.neo4j.rest;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	RestAPI graphDb = new RestAPIFacade("http://localhost:7474/db/data");

    	QueryEngine engine=new RestCypherQueryEngine(graphDb);  
    	 QueryResult<Map<String,Object>> result=  
    	     engine.query("start n=node(*) return count(n) as total", Collections.EMPTY_MAP);  
    	 Iterator<Map<String, Object>> iterator=result.iterator();  
    	 if(iterator.hasNext()) {  
    	   Map<String,Object> row= iterator.next();  
    	   System.out.println("Total nodes: " + row.get("total"));  
    	 }
        assertTrue( true );
    }
}

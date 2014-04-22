package service.impl;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;
import service.CypherCallback;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ionut on 19.04.2014.
 */
public class Neo4JHelper {

    private GraphDatabaseService graphDatabaseService;
    private QueryEngine queryEngine;
    private ExecutionEngine executionEngine;

    public Neo4JHelper(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
        if (graphDatabaseService instanceof EmbeddedGraphDatabase) {
            executionEngine = new ExecutionEngine(graphDatabaseService);

        } else if (graphDatabaseService instanceof RestGraphDatabase) {
            queryEngine = new RestCypherQueryEngine(((RestGraphDatabase) graphDatabaseService).getRestAPI());

        } else
            throw new RuntimeException("[findURLs] Not suppoted for " + graphDatabaseService.getClass().getName());
    }

    public void execute(String query, Map<String, Object> params, CypherCallback callback) {
        if (params == null) {
            params = new HashMap<>();
        }
        //        try (Transaction ignored = graphDatabaseService.beginTx())
//        {
            if (graphDatabaseService instanceof EmbeddedGraphDatabase) {
                ExecutionResult result = executionEngine.execute(query, params);
                for (Map<String, Object> props : result) {
                    callback.execute(props);
                }
            } else if (graphDatabaseService instanceof RestGraphDatabase) {
                QueryResult<Map<String, Object>> result = queryEngine.query(query, params);
                for (Map<String, Object> props : result) {
                    callback.execute(props);
                }
            } else
                throw new RuntimeException("[findURLs] Not suppoted for " + graphDatabaseService.getClass().getName());
//        }
    }

}

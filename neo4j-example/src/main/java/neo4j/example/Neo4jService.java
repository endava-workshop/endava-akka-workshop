package neo4j.example;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Neo4jService {

	private final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";

	public URI createNode(String nodeName, String nodeUrl) {
		final String nodeEntryPointUri = SERVER_ROOT_URI + "node";
		// http://localhost:7474/db/data/node

		WebResource resource = Client.create().resource(nodeEntryPointUri);
		// POST {} to the node entry point URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity("{}")
				.post(ClientResponse.class);

		final URI location = response.getLocation();
		System.out.println(String.format(
				"POST to [%s], status code [%d], location header [%s]",
				nodeEntryPointUri, response.getStatus(), location.toString()));
		response.close();

		addProperty(location, "name", nodeName);
		addProperty(location, "url", nodeUrl);
		
		return location;
	}

	public URI addRelationship( URI startNode, URI endNode,
            String relationshipType, String jsonAttributes )
            throws URISyntaxException
    {
        URI fromUri = new URI( startNode.toString() + "/relationships" );
        String relationshipJson = generateJsonRelationship( endNode,
                relationshipType, jsonAttributes );

        WebResource resource = Client.create()
                .resource( fromUri );
        // POST JSON to the relationships URI
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( relationshipJson )
                .post( ClientResponse.class );

        final URI location = response.getLocation();
        System.out.println( String.format(
                "POST to [%s], status code [%d], location header [%s]",
                fromUri, response.getStatus(), location.toString() ) );

        response.close();
        return location;
    }
	
	public boolean checkDatabaseIsRunning() {
		WebResource resource = Client.create().resource(SERVER_ROOT_URI);
		ClientResponse response = resource.get(ClientResponse.class);

		System.out.println(String.format("GET on [%s], status code [%d]",
				SERVER_ROOT_URI, response.getStatus()));

		int status = response.getStatus();
		response.close();

		return (status == 200);
	}

	private void addProperty(URI nodeUri, String propertyName,
			String propertyValue) {
		String propertyUri = nodeUri.toString() + "/properties/" + propertyName;
		// http://localhost:7474/db/data/node/{node_id}/properties/{property_name}

		WebResource resource = Client.create().resource(propertyUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.entity("\"" + propertyValue + "\"").put(ClientResponse.class);

		System.out.println(String.format("PUT to [%s], status code [%d]",
				propertyUri, response.getStatus()));
		response.close();
	}

    private String generateJsonRelationship( URI endNode,
            String relationshipType, String... jsonAttributes )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "{ \"to\" : \"" );
        sb.append( endNode.toString() );
        sb.append( "\", " );

        sb.append( "\"type\" : \"" );
        sb.append( relationshipType );
        if ( jsonAttributes == null || jsonAttributes.length < 1 )
        {
            sb.append( "\"" );
        }
        else
        {
            sb.append( "\", \"data\" : " );
            for ( int i = 0; i < jsonAttributes.length; i++ )
            {
                sb.append( jsonAttributes[i] );
                if ( i < jsonAttributes.length - 1 )
                { // Miss off the final comma
                    sb.append( ", " );
                }
            }
        }

        sb.append( " }" );
        return sb.toString();
    }
}

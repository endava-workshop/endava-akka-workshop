package ro.endava.akka.workshop.es.responses;

import com.google.gson.JsonObject;

/**
 * Created by cosmin on 4/9/14.
 */
public class ESHits {
    private String _index;
    private String _type;
    private String _id;
    private Long _version;
    private Double _score;
    private JsonObject _source;
}

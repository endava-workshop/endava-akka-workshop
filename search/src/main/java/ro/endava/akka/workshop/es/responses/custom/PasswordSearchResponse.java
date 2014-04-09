package ro.endava.akka.workshop.es.responses.custom;

import com.google.gson.JsonObject;


/**
 * Created by cosmin on 3/13/14.
 * Response for search actions for passwords from ES
 */
public class PasswordSearchResponse{

    private Long took;
    private Boolean timed_out;
    private JsonObject _shards;
    private PasswordSearchHits hits;

    public PasswordSearchResponse() {
    }

    public Long getTook() {
        return took;
    }

    public Boolean getTimed_out() {
        return timed_out;
    }

    public JsonObject get_shards() {
        return _shards;
    }

    public PasswordSearchHits getHits() {
        return hits;
    }
}

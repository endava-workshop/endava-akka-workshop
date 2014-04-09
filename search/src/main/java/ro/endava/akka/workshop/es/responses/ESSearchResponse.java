package ro.endava.akka.workshop.es.responses;

import com.google.gson.JsonObject;


/**
 * Created by cosmin on 3/13/14.
 * Response for search actions from ES
 */
public class ESSearchResponse {

    private Long took;
    private Boolean timed_out;
    private JsonObject _shards;
    private ESSearchHits hits;

    public ESSearchResponse() {
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

    public ESSearchHits getHits() {
        return hits;
    }
}

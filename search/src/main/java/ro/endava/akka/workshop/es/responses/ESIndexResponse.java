package ro.endava.akka.workshop.es.responses;

/**
 * Created by cvasii on 4/14/14.
 */
public class ESIndexResponse {
    private Boolean acknowledged;

    public ESIndexResponse() {
    }

    public ESIndexResponse(Boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public Boolean getAcknowledged() {
        return acknowledged;
    }
}

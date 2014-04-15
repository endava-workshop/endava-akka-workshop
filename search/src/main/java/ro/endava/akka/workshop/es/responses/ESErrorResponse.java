package ro.endava.akka.workshop.es.responses;

/**
 * Created by cvasii on 4/14/14.
 */
public class ESErrorResponse {
    private String error;

    private Integer status;

    public ESErrorResponse() {
    }

    public ESErrorResponse(String error, Integer status) {
        this.error = error;
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public Integer getStatus() {
        return status;
    }
}

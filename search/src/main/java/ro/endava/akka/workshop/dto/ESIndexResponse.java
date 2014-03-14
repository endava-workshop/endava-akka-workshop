package ro.endava.akka.workshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by cosmin on 3/11/14.
 * DTO representing the response in case of successful indexing a document in elastic search
 */
public class ESIndexResponse implements Serializable {

    @JsonProperty(value = "_index")
    private String index;

    @JsonProperty(value = "_type")
    private String type;

    @JsonProperty(value = "_id")
    private String id;

    @JsonProperty(value = "_version")
    private String version;

    @JsonProperty(value = "created")
    private String created;

    public ESIndexResponse() {
    }

    public ESIndexResponse(String index, String type, String id, String version, String created) {
        this.index = index;
        this.type = type;
        this.id = id;
        this.version = version;
        this.created = created;
    }

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "ESIndexResponse{" +
                "index='" + index + '\'' +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", created='" + created + '\'' +
                '}';
    }
}

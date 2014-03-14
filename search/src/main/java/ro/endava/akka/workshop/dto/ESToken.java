package ro.endava.akka.workshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by cosmin on 3/13/14.
 * DTo representing the token received from ES server after an analyze request
 */
public class ESToken {

    @JsonProperty(value = "token")
    private String token;

    @JsonProperty(value = "start_offset")
    private Integer startOffset;

    @JsonProperty(value = "end_offset")
    private Integer endOffset;

    @JsonProperty(value = "type")
    private String type;

    @JsonProperty(value = "position")
    private Integer position;

    public ESToken() {
    }

    public ESToken(String token, Integer startOffset, Integer endOffset, String type, Integer position) {
        this.token = token;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.type = type;
        this.position = position;
    }

    public String getToken() {
        return token;
    }

    public Integer getStartOffset() {
        return startOffset;
    }

    public Integer getEndOffset() {
        return endOffset;
    }

    public String getType() {
        return type;
    }

    public Integer getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "ESToken{" +
                "token='" + token + '\'' +
                ", startOffset=" + startOffset +
                ", endOffset=" + endOffset +
                ", type='" + type + '\'' +
                ", position=" + position +
                '}';
    }
}

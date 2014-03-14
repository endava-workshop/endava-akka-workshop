package ro.endava.akka.workshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by cosmin on 3/13/14.
 */
public class ESAnalyzeResponse {

    @JsonProperty(value = "tokens")
    private List<ESToken> tokens;

    public ESAnalyzeResponse() {
    }

    public ESAnalyzeResponse(List<ESToken> tokens) {
        this.tokens = tokens;
    }

    public List<ESToken> getTokens() {
        return tokens;
    }

    @Override
    public String toString() {
        return "ESAnalyzeResponse{" +
                "tokens=" + tokens +
                '}';
    }
}

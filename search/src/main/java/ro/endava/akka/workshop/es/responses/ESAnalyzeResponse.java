package ro.endava.akka.workshop.es.responses;

import java.util.List;

/**
 * Created by cosmin on 3/13/14.
 * Response for analyze actions from ES
 */
public class ESAnalyzeResponse {

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

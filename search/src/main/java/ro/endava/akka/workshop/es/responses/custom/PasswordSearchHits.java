package ro.endava.akka.workshop.es.responses.custom;

import java.util.List;

/**
 * Created by cosmin on 4/9/14.
 */
public class PasswordSearchHits {
    private Long total;
    private Double max_score;
    private List<PasswordHits> hits;

    public Long getTotal() {
        return total;
    }

    public Double getMax_score() {
        return max_score;
    }

    public List<PasswordHits> getHits() {
        return hits;
    }
}

package ro.endava.akka.workshop.es.responses;

import java.util.List;

/**
 * Created by cosmin on 4/9/14.
 */
public class ESSearchHits {
    private Long total;
    private Double max_score;
    private List<ESHits> hits;
}

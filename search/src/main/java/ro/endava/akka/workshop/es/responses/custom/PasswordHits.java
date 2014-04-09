package ro.endava.akka.workshop.es.responses.custom;

/**
 * Created by cosmin on 4/9/14.
 */
public class PasswordHits {
    private String _index;
    private String _type;
    private String _id;
    private Long _version;
    private Double _score;
    private Password _source;

    public String get_index() {
        return _index;
    }

    public String get_type() {
        return _type;
    }

    public String get_id() {
        return _id;
    }

    public Long get_version() {
        return _version;
    }

    public Double get_score() {
        return _score;
    }

    public Password get_source() {
        return _source;
    }
}

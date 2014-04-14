package ro.endava.akka.workshop.es.actions.structures;

import java.util.Map;

/**
 * Created by cvasii on 4/14/14.
 */
public class ESAnalyzer {

    private String name;
    private Map<String, Object> props;

    public ESAnalyzer(String name, Map<String, Object> props) {
        this.name = name;
        this.props = props;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getProps() {
        return props;
    }
}

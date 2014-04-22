package service;

import java.util.Map;

/**
 * Created by ionut on 19.04.2014.
 */
public interface CypherCallback {
    CypherCallback NONE = new CypherCallback () {
        @Override public void execute(Map<String, Object> props) {
            // NOOP
        }
    };

    void execute(Map<String, Object> props);
}
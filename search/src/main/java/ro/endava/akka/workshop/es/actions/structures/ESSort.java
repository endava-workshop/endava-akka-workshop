package ro.endava.akka.workshop.es.actions.structures;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cosmin on 5/11/14.
 */
public class ESSort {

    public enum SortType {ASC, DESC}

    private String field;
    private SortType direction;

    public ESSort(String field) {
        this.field = field;
    }

    public ESSort(String field, SortType direction) {
        this.direction = direction;
        this.field = field;
    }

    public String toString() {
        // simple case
        if (direction == null)
            return "\"" + this.field + "\"";

        // build of complex cases

        Map<String, Object> obj = new HashMap<String, Object>();

        if (direction != null) {
            String dir = "asc";
            if (direction == SortType.DESC)
                dir = "desc";
            obj.put("order", dir);
        }


        String json = new Gson().toJson(obj);

        return "{ \"" + this.field + "\" : " + json + "}";
    }
}

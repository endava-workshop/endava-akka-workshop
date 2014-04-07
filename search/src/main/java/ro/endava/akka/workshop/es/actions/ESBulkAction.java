package ro.endava.akka.workshop.es.actions;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cosmin on 4/6/14.
 * Bulk action for ES
 */
public class ESBulkAction extends ESAbstractAction {

    private Collection<ESBulky> bulkies;
    private Gson gson;

    private ESBulkAction(Builder builder) {
        super(builder);
        this.bulkies = builder.bulkies;
        this.gson = builder.gson;
        if (this.gson == null) {
            this.gson = new Gson();
        }
        this.url = buildUrl();
        this.body = buildBody();
        this.method = "POST";
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public Object getBody() {
        return body;
    }

    protected String buildUrl() {
        StringBuilder sb = new StringBuilder("_bulk");
        String uri = sb.toString();
        return uri;
    }

    private Object buildBody() {
 /*
        { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
        { "field1" : "value1" }
        { "delete" : { "_index" : "test", "_type" : "type1", "_id" : "2" } }
         */
        StringBuilder sb = new StringBuilder();
        for (ESBulky bulky : bulkies) {
            // write out the bulky-meta-data line
            // e.g.: { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
            Map<String, Map<String, String>> opMap = new HashMap<String, Map<String, String>>(1);

            Map<String, String> opDetails = new HashMap<String, String>(3);
            if (StringUtils.isNotBlank(bulky.getIndex())) {
                opDetails.put("_index", bulky.getIndex());
            }
            if (StringUtils.isNotBlank(bulky.getType())) {
                opDetails.put("_type", bulky.getType());
            }
            if (StringUtils.isNotBlank(bulky.getId())) {
                opDetails.put("_id", bulky.getId());
            }

            opMap.put(bulky.getESActionName(), opDetails);
            sb.append(gson.toJson(opMap, new TypeToken<Map<String, Map<String, String>>>() {
            }.getType()));
            sb.append("\n");

            // write out the bulky source/document line
            // e.g.: { "field1" : "value1" }
            Object source = bulky.getBody();
            if (source != null) {
                sb.append(getJson(gson, source));
                sb.append("\n");
            }

        }
        return sb.toString();
    }

    private Object getJson(Gson gson, Object source) {
        if (source instanceof String) {
            return source;
        } else {
            return gson.toJson(source);
        }
    }

    public static class Builder extends ESAbstractAction.Builder<ESBulkAction, Builder> {
        private Collection<ESBulky> bulkies = new ArrayList<>();
        private Gson gson;

        public Builder bulky(ESBulky bulky) {
            this.bulkies.add(bulky);
            return this;
        }

        public Builder bulkies(Collection<ESBulky> bulkies) {
            this.bulkies.addAll(bulkies);
            return this;
        }

        public Builder gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public ESBulkAction build() {
            return new ESBulkAction(this);
        }
    }
}

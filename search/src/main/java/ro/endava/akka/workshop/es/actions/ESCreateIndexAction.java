package ro.endava.akka.workshop.es.actions;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import ro.endava.akka.workshop.es.actions.structures.ESAnalyzer;
import ro.endava.akka.workshop.es.actions.structures.ESFilter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cosmin on 4/6/14.
 * Create index in ES
 * Specify the index name
 * Just creating the index without settings
 */
public class ESCreateIndexAction extends ESAbstractAction {

    private List<ESAnalyzer> analyzers = new ArrayList<>();
    private List<ESFilter> filters = new ArrayList<>();

    private ESCreateIndexAction(Builder builder) {
        super(builder);
        this.analyzers = builder.analyzers;
        this.filters = builder.filters;
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

    private String buildBody() {
        XContentBuilder builder = null;
        String result = null;
        try {
            builder = XContentFactory.jsonBuilder().startObject().startObject("settings").startObject("index").startObject("analysis");

            if (analyzers.size() > 0) {
                builder.startObject("analyzer");

                for (ESAnalyzer analyzer : analyzers) {
                    builder.startObject(analyzer.getName());
                    Map<String, Object> props = analyzer.getProps();
                    for (Map.Entry<String, Object> entry : props.entrySet()) {
                        if (entry.getValue() instanceof List) {
                            List list = (List) entry.getValue();
                            builder.array(entry.getKey(), list.toArray());
                        } else {
                            builder.field(entry.getKey(), entry.getValue());
                        }
                    }
                    builder.endObject();
                }
                builder.endObject();
            }

            if(filters.size() > 0){
                builder.startObject("filter");
                for(ESFilter filter : filters){
                    builder.startObject(filter.getName());
                    Map<String, Object> props = filter.getProps();
                    for (Map.Entry<String, Object> entry : props.entrySet()) {
                        if (entry.getValue() instanceof List) {
                            List list = (List) entry.getValue();
                            builder.array(entry.getKey(), list.toArray());
                        } else {
                            builder.field(entry.getKey(), entry.getValue());
                        }
                    }
                    builder.endObject();
                }
                builder.endObject();
            }

            builder.endObject().endObject().endObject().endObject();
            result = builder.string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected String buildUrl() {
        StringBuilder sb = new StringBuilder();
        try {
            if (StringUtils.isNotBlank(index)) {
                sb.append(URLEncoder.encode(index, ENCODING)).append("/");
            }
        } catch (UnsupportedEncodingException e) {
            //
        }
        String uri = sb.toString();
        return uri;
    }

    public static class Builder extends ESAbstractAction.Builder<ESCreateIndexAction, Builder> {
        private List<ESAnalyzer> analyzers = new ArrayList<>();
        private List<ESFilter> filters = new ArrayList<>();

        public Builder analyzer(ESAnalyzer analyzer) {
            this.analyzers.add(analyzer);
            return this;
        }

        public Builder filter(ESFilter filter) {
            this.filters.add(filter);
            return this;
        }

        public ESCreateIndexAction build() {
            return new ESCreateIndexAction(this);
        }
    }
}

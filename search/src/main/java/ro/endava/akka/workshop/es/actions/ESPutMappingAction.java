package ro.endava.akka.workshop.es.actions;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import ro.endava.akka.workshop.es.actions.structures.ESMapping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cosmin on 4/6/14.
 * Put settings for an index in ES
 * Just putting the type of a fields in mapping for now
 */
public class ESPutMappingAction extends ESAbstractAction {

    List<ESMapping> mappings = new ArrayList<>();

    private ESPutMappingAction(Builder builder) {
        super(builder);
        this.mappings = builder.mappings;
        this.url = buildUrl();
        this.body = this.buildBody();
        this.method = "PUT";
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
            builder = XContentFactory.jsonBuilder().startObject().startObject(this.getType()).startObject("properties");
            for (ESMapping mapping : mappings) {
                builder.startObject(mapping.getAttrName());
                if (mapping.getAttrName() != null) {
                    builder.field("type", mapping.getAttrType());
                }
                builder.endObject();
            }
            builder.endObject().endObject().endObject();
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
                if (StringUtils.isNotBlank(type)) {
                    sb.append(URLEncoder.encode(type, ENCODING)).append("/");
                }
            }
        } catch (UnsupportedEncodingException e) {
            //
        }
        sb.append("_mapping");
        String uri = sb.toString();
        return uri;
    }

    public static class Builder extends ESAbstractAction.Builder<ESPutMappingAction, Builder> {

        private List<ESMapping> mappings = new ArrayList<>();

        public Builder attribute(String attrName, String attrType) {
            ESMapping mapping = new ESMapping(attrName, attrType);
            mappings.add(mapping);
            return this;
        }

        public ESPutMappingAction build() {
            return new ESPutMappingAction(this);
        }
    }
}

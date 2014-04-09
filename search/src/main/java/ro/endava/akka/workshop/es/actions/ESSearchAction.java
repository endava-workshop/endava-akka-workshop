package ro.endava.akka.workshop.es.actions;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by cosmin on 4/6/14.
 * Indexing action for ES
 */
//TODO add sorting
public class ESSearchAction extends ESAbstractAction {

    private String query;
    private Long from;
    private Long size;

    private ESSearchAction(Builder builder) {
        super(builder);
        this.query = builder.query;
        this.from = builder.from;
        this.size = builder.size;
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
        StringBuilder version = new StringBuilder("\"version\": " + true);
        if (query == null) {
            try {
                XContentBuilder builder = JsonXContent.contentBuilder();
                builder.startObject();
                builder.endObject();
                query = builder.string();

            } catch (IOException e) {
                //
            }
        }
        else{
            version.append(",");
        }

        query = query.replaceFirst("\\{", "\\{" + version.toString());
        if (from != null) {
            StringBuilder fromString = new StringBuilder("\"from\": ");
            fromString.append(String.valueOf(from)).append(",");
            query = query.replaceFirst("\\{", "\\{" + fromString.toString());
        }

        if (size != null) {
            StringBuilder sizeString = new StringBuilder("\"size\": ");
            sizeString.append(String.valueOf(size)).append(",");
            query = query.replaceFirst("\\{", "\\{" + sizeString.toString());
        }


        return query;
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
        sb.append("_search");
        String uri = sb.toString();
        return uri;
    }

    public static class Builder extends ESAbstractAction.Builder<ESSearchAction, Builder> {
        private String query;
        private Long from;
        private Long size;

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder from(Long from) {
            this.from = from;
            return this;
        }

        public Builder size(Long size) {
            this.size = size;
            return this;
        }

        public ESSearchAction build() {
            return new ESSearchAction(this);
        }
    }
}

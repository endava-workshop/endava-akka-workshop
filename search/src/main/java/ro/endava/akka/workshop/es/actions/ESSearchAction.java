package ro.endava.akka.workshop.es.actions;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by cosmin on 4/6/14.
 * Indexing action for ES
 */
//TODO add sorting
//TODO add pagination features
public class ESSearchAction extends ESAbstractAction {

    private String query;
    private ESQueryType queryType;

    private ESSearchAction(Builder builder) {
        super(builder);
        this.query = builder.query;
        this.queryType = builder.queryType;
        this.url = buildUrl();
        this.body = builder.body;
        this.method = (id != null) ? "PUT" : "POST";
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public Object getBody() {
        return body;
    }

    //TODO append the query type to the url
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
        private ESQueryType queryType;

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder queryType(ESQueryType queryType) {
            this.queryType = queryType;
            return this;
        }

        public ESSearchAction build() {
            return new ESSearchAction(this);
        }
    }
}

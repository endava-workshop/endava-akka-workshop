package ro.endava.akka.workshop.es.actions;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by cosmin on 4/6/14.
 */
public class ESAnalyzeAction extends ESAbstractAction {

    private Object source;
    private String analyzer;
    private String method;

    private ESAnalyzeAction(Builder builder) {
        super(builder);
        this.source = builder.source;
        this.analyzer = builder.analyzer;
        this.url = buildUrl();
        this.method = "POST";
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public Object getBody() {
        return source;
    }

    public Object getSource() {
        return source;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    @Override
    protected String buildUrl() {

        StringBuilder sb = new StringBuilder();
        sb.append(super.buildUrl()).append("/_analyze");
        sb.append("?analyzer=");
        sb.append(analyzer);
        return sb.toString();

    }

    public static class Builder extends ESAbstractAction.Builder<ESAnalyzeAction, Builder> {
        private Object source;
        private String analyzer;

        public Builder source(Object source) {
            this.source = source;
            return this;
        }

        public Builder analyzer(String analyzer) {
            this.analyzer = analyzer;
            return this;
        }

        public ESAnalyzeAction build() {
            return new ESAnalyzeAction(this);
        }
    }
}

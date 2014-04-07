package ro.endava.akka.workshop.es.actions;

/**
 * Created by cosmin on 4/6/14.
 * Analyze action for ES
 */
public class ESAnalyzeAction extends ESAbstractAction {

    private String analyzer;

    private ESAnalyzeAction(Builder builder) {
        super(builder);
        this.body = builder.body;
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
        return body;
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
        private String analyzer;

        public Builder analyzer(String analyzer) {
            this.analyzer = analyzer;
            return this;
        }

        public ESAnalyzeAction build() {
            return new ESAnalyzeAction(this);
        }
    }
}

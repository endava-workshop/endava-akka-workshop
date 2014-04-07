package ro.endava.akka.workshop.es.actions;

/**
 * Created by cosmin on 4/6/14.
 */
public class ESIndexAction extends ESAbstractAction {

    private Object body;
    private String method;

    private ESIndexAction(Builder builder) {
        super(builder);
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

    public static class Builder extends ESAbstractAction.Builder<ESIndexAction, Builder> {
        private Object body;

        public Builder body(Object body) {
            this.body = body;
            return this;
        }

        public ESIndexAction build() {
            return new ESIndexAction(this);
        }
    }
}

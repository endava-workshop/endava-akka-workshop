package ro.endava.akka.workshop.es.actions;

/**
 * Created by cosmin on 4/6/14.
 * Indexing action for ES
 */
public class ESIndexAction extends ESAbstractAction {

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
        public ESIndexAction build() {
            return new ESIndexAction(this);
        }
    }
}

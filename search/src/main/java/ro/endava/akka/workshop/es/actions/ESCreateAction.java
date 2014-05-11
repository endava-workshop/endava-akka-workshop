package ro.endava.akka.workshop.es.actions;

/**
 * Created by cosmin on 4/6/14.
 * Creating action for ES
 */
public class ESCreateAction extends ESAbstractAction implements ESBulky{

    private ESCreateAction(Builder builder) {
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

    @Override
    public String getESActionName() {
        return "create";
    }

    public static class Builder extends ESAbstractAction.Builder<ESCreateAction, Builder> {
        public ESCreateAction build() {
            return new ESCreateAction(this);
        }
    }
}

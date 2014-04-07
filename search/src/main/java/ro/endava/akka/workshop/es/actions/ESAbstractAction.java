package ro.endava.akka.workshop.es.actions;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by cosmin on 4/6/14.
 * Abstract class for default implementation of actions done on ES
 */
public abstract class ESAbstractAction implements ESAction {

    public final static String ENCODING = "utf-8";

    protected String index;
    protected String type;
    protected String id;
    protected String url;
    protected Object body;
    protected String method;

    protected ESAbstractAction(Builder builder) {
        this.index = builder.index;
        this.type = builder.type;
        this.id = builder.id;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getIndex() {
        return index;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getMethod() {
        return null;
    }

    @Override
    public Object getBody() {
        return null;
    }

    protected String buildUrl() {
        StringBuilder sb = new StringBuilder();

        try {
            if (StringUtils.isNotBlank(index)) {
                sb.append(URLEncoder.encode(index, ENCODING));

                if (StringUtils.isNotBlank(type)) {
                    sb.append("/").append(URLEncoder.encode(type, ENCODING));

                    if (StringUtils.isNotBlank(id)) {
                        sb.append("/").append(URLEncoder.encode(id, ENCODING));
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            //
        }

        String uri = sb.toString();
        return uri;
    }

    protected static abstract class Builder<T extends ESAction, E> {
        protected String index;
        protected String type;
        protected String id;
        protected Object body;


        abstract public T build();

        public E index(String index) {
            this.index = index;
            return (E) this;
        }

        public E type(String type) {
            this.type = type;
            return (E) this;
        }

        public E id(String id) {
            this.id = id;
            return (E) this;
        }

        public E body(Object body) {
            this.body = body;
            return (E) this;
        }

    }
}

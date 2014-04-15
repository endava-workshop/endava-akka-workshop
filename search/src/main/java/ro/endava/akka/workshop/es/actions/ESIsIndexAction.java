package ro.endava.akka.workshop.es.actions;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by cosmin on 4/6/14.
 * Bulk action for ES
 */
public class ESIsIndexAction extends ESAbstractAction {


    private ESIsIndexAction(Builder builder) {
        super(builder);
        this.url = buildUrl();
        this.body = null;
        this.method = "GET";
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public Object getBody() {
        return body;
    }

    protected String buildUrl() {
        StringBuilder sb = new StringBuilder();
        try {
            if (StringUtils.isNotBlank(index)) {
                sb.append(URLEncoder.encode(index, ENCODING));
            }
            else {
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            //
        }
        sb.append("/_status");
        String uri = sb.toString();
        return uri;
    }



    public static class Builder extends ESAbstractAction.Builder<ESIsIndexAction, Builder> {

        public ESIsIndexAction build() {
            return new ESIsIndexAction(this);
        }
    }
}

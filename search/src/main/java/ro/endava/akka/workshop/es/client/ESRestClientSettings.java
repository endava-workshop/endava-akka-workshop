package ro.endava.akka.workshop.es.client;

import com.google.gson.Gson;

/**
 * Created by cosmin on 4/6/14.
 */
public class ESRestClientSettings {

    private String server;
    private Gson gson;

    public String getServer() {
        return server;
    }

    public Gson getGson() {
        return gson;
    }

    public ESRestClientSettings(Builder builder) {
        this.server = builder.server;
        this.gson = builder.gson;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String server;
        private Gson gson;

        private Builder() {
        }

        public ESRestClientSettings build() {
            return new ESRestClientSettings(this);
        }

        public Builder server(String server) {
            this.server = server;
            return this;
        }

        public Builder gson(Gson gson) {
            this.gson = gson;
            return this;
        }
    }
}

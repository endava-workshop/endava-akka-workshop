package ro.endava.akka.workshop.es.responses;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cosmin on 4/6/14.
 * General response from ES
 */
public class ESResponse {

    private final static Logger LOGGER = LoggerFactory.getLogger(ESResponse.class);

    private JsonObject jsonObject;
    private String jsonString;
    private boolean isOk;
    private String errorMessage;
    private Gson gson;

    public ESResponse(JsonObject jsonObject, String jsonString, boolean isOk, String errorMessage, Gson gson) {
        this.jsonObject = jsonObject;
        this.jsonString = jsonString;
        this.isOk = isOk;
        this.errorMessage = errorMessage;
        this.gson = gson;
    }

    public <T> T getSourceAsObject(Class<T> clazz) {
        JsonArray sourceList = extractSource();
        if (sourceList.size() > 0)
            return createSourceObject(sourceList.get(0), clazz);
        else
            return null;
    }

    private <T> T createSourceObject(JsonElement source, Class<T> type) {
        T obj = null;
        try {
            String json = source.toString();
            obj = gson.fromJson(json, type);
        } catch (Exception e) {
            LOGGER.error("Unhandled exception occurred while converting source to the object ." + type.getCanonicalName(), e);
        }
        return (T) obj;
    }


    private JsonArray extractSource() {
        JsonArray sourceList = new JsonArray();
        if (jsonObject == null)
            return sourceList;
        sourceList.add(jsonObject);
        return sourceList;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public String getJsonString() {
        return jsonString;
    }

    public boolean isOk() {
        return isOk;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Gson getGson() {
        return gson;
    }
}

package service.impl.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.jongo.marshall.jackson.oid.Id;

public class MongoSimpleURL {

    private ObjectId _id;

//    @Id
    String url;
    String name;
    String domain;
    String sourceDomain;
    String status; // VISITED, NOT_VISITED, ERROR
    int errorCount;
    long lastUpdate; // epoch

    public MongoSimpleURL(String url, String name, String domain, String sourceDomain, String status, int errorCount, long lastUpdate) {
        this.lastUpdate = lastUpdate;
        this.errorCount = errorCount;
        this.status = status;
        this.name = name;
        this.domain = domain;
        this.sourceDomain = sourceDomain;
        this.url = url;
    }

    public MongoSimpleURL() {
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSourceDomain() {
        return sourceDomain;
    }

    public void setSourceDomain(String sourceDomain) {
        this.sourceDomain = sourceDomain;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
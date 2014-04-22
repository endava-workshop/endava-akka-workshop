package entity;

import org.neo4j.graphdb.Node;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.support.index.IndexType;

@NodeEntity
public class SimpleURL {

	@GraphId Long id;
	
    @Indexed(unique = true, indexType = IndexType.SIMPLE, indexName = "url")
	String url;
    String name;
    String status; // VISITED, NOT_VISITED, ERROR
    int errorCount;
    long lastUpdate; // epoch

	public SimpleURL() {
		
	}
	
	private static long getLongValue(Node node, String key) {
        Object value = node.getProperty(key, null);
        if (value != null) {
            return Long.parseLong(value.toString());
        }
        return 0;
    }
	private static int getIntValue(Node node, String key) {
        Object value = node.getProperty(key, null);
        if (value != null) {
            return Integer.parseInt(value.toString());
        }
        return 0;
    }
	private static String getStringValue(Node node, String key) {
        Object value = node.getProperty(key, null);
        if (value != null) {
            return value.toString();
        }
        return null;
    }
	public SimpleURL(Node node) {
        this(getStringValue(node, "url"),
                getStringValue(node, "name"),
                getStringValue(node, "status"),
                getIntValue(node, "errorCount"),
                getLongValue(node, "lastUpdate")
        );

	}

    public SimpleURL(String url, String name, String status) {
        this(url, name, status, 0, System.currentTimeMillis());
    }

    public SimpleURL(String url, String name, String status, int errorCount, long lastUpdate) {
        this.url = url;
        this.name = name;
        this.status = status;
        this.errorCount = errorCount;
        this.lastUpdate = lastUpdate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "SimpleURL{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", errorCount=" + errorCount +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}

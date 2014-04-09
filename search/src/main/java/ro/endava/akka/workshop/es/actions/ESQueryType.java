package ro.endava.akka.workshop.es.actions;

/**
 * Created by cosmin on 4/7/14.
 */
public enum ESQueryType {
    DFS_QUERY_THEN_FETCH("dfs_query_then_fetch"), FS_QUERY_AND_FETCH("fs_query_and_fetch"),
    QUERY_THEN_FETCH("query_then_fetch"), QUERY_AND_FETCH("query_and_fetch"), COUNT("count"),
    SCAN("scan");
    private String value;

    ESQueryType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}

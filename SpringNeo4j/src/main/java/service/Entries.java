package service;

import java.util.ArrayList;
import java.util.List;

//  debug purpose: extract data out of neo4j using simple queries
public class Entries {
    String k;
    private List<Entry> entries = new ArrayList<>();

    public Entries(String k, List<Entry> entries) {
        this.k = k;
        this.entries = entries;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
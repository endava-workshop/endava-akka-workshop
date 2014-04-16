package com.en_workshop.webcrawlerakka.entities;

/**
 * Link for Elastic Search integration. It contains the URL and the content retrieved from that address.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 */
public class Page {


    private String url;
    private String content;

    public Page(String url, String content) {
        this.url = url;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

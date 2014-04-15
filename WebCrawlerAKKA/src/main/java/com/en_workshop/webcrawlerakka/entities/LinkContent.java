package com.en_workshop.webcrawlerakka.entities;

/**
 * Entity for the content of a link.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 */
public class LinkContent {

    private Link link;
    private String content;

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

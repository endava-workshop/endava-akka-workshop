package ro.endava.akka.workshop.messages;

import java.io.Serializable;

/**
 * Created by cosmin on 3/10/14.
 * Class representing the article that will get indexed.
 * Holds the domain, the content of the article, a list with the external links
 * and a list with domain links.
 */
public class IndexMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String domain;
    private String content;

    public IndexMessage(String domain, String content) {
        this.domain = domain;
        this.content = content;
    }

    public String getDomain() {
        return domain;
    }

    public String getContent() {
        return content;
    }


    @Override
    public String toString() {
        return "IndexMessage{" +
                "domain='" + domain + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

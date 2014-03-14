package ro.endava.akka.workshop.messages;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

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
    private List<String> domainLinks;
    private List<String> externalLinks;

    public IndexMessage(String domain, String content, List<String> domainLinks, List<String> externalLinks) {
        this.domain = domain;
        this.content = content;
        this.domainLinks = Collections.unmodifiableList(domainLinks);
        this.externalLinks = Collections.unmodifiableList(externalLinks);
    }

    public String getDomain() {
        return domain;
    }

    public String getContent() {
        return content;
    }

    public List<String> getDomainLinks() {
        return domainLinks;
    }

    public List<String> getExternalLinks() {
        return externalLinks;
    }

    @Override
    public String toString() {
        return "IndexMessage{" +
                "domain='" + domain + '\'' +
                ", content='" + content + '\'' +
                ", domainLinks=" + domainLinks +
                ", externalLinks=" + externalLinks +
                '}';
    }
}

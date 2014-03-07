package com.en_workshop.webcrawlerakka.entities;

import com.en_workshop.webcrawlerakka.enums.WebUrlStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Radu Ciumag
 */
public class WebUrl {

    public static final List<WebUrl> URLS = new ArrayList<>();

    private final WebDomain webDomain;
    private final String url;
    private final WebUrlStatus status;

    public WebUrl(final WebDomain webDomain, final String url) {
        this(webDomain, url, WebUrlStatus.NOT_VISITED);
    }

    public WebUrl(final WebDomain webDomain, final String url, final WebUrlStatus status) {
        this.webDomain = webDomain;
        this.url = url;
        this.status = status;
    }

    public WebDomain getWebDomain() {
        return webDomain;
    }

    public String getUrl() {
        return url;
    }

    public WebUrlStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "WebUrl{" +
                "webDomain=" + webDomain +
                ", url='" + url + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WebUrl webUrl = (WebUrl) o;
        if (url != null ? !url.equals(webUrl.url) : webUrl.url != null) {
            return false;
        }
        if (webDomain != null ? !webDomain.equals(webUrl.webDomain) : webUrl.webDomain != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = webDomain != null ? webDomain.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);

        return result;
    }
}

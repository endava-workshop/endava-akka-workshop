package com.en_workshop.webcrawlerakka.integration.elasticsearch;

/**
 * Link for Elastic Search integration. It contains the URL and the content retrieved from that address.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/7/14
 */
public class Link {

	private String url;

	private String content;

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

package service.impl.neo4j.entity;

public class Domain {

	private String url;
	
	private String name;
	
	private long cooldownPeriod;

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

	public long getCooldownPeriod() {
		return cooldownPeriod;
	}

	public void setCooldownPeriod(long cooldownPeriod) {
		this.cooldownPeriod = cooldownPeriod;
	}

}

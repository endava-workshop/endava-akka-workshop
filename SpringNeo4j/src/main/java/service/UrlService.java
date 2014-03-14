package service;

import entity.DomainUrl;
import entity.SimpleUrl;

public interface UrlService {

	public DomainUrl addDomainUrl(String domainName, String domainUrl);
	
	public SimpleUrl addSimpleUrl(String name, String url, String domainName);
	
	public void removeSimpleUrl(String simpleUrl) ;
	public void removeDomainUrl(String domainUrl) ;

	public void removeAllDomains();
}

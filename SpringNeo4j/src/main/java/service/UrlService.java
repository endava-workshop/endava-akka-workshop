package service;

public interface UrlService {

	public void addDomainUrl(String urlValue) ;
	public void addSimpleUrl(String urlValue, String domainUrl); 
	public void removeSimpleUrl(String simpleUrl) ;
	public void removeDomainUrl(String domainUrl) ;
}

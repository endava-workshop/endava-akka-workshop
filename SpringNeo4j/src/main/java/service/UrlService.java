package service;

import entity.DomainLink;
import entity.DomainURL;
import entity.SimpleURL;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface UrlService {

    public DomainURL addDomainUrl(String domainName, String domainUrl, long coolDownPeriod);
    List<DomainURL> findDomains(Pageable pageable);
    public void removeDomainUrl(String domainUrl) ;
    public void removeDomains();

	public void addSimpleUrls(List<String> urls, String status, String domainURL, String sourceDomainName);
    public void addDomainLinks(List<DomainLink> domainLinks);
    public void updateSimpleUrlsStatus(List<String> urls, String status);
    public void updateSimpleUrlErrorStatus(String url, int errorDelta);
	Collection<SimpleURL> findURLs(String address,String status, int pageNo, int pageSize);
	Collection<SimpleURL> findExternalURLs(String address,String status, int pageNo, int pageSize);
	public void removeSimpleUrl(String simpleUrl) ;

    //  debug purpose: extract data out of neo4j using simple queries
    public List<Entries> query(String query);

    public long countAllNodes();
    
    public long countAllDomains();
    
    public long countAllLinks();
    
    public long countDomainLinks(String domainUrl);
}

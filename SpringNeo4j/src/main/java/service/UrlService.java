package service;

import entity.DomainURL;
import entity.SimpleURL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UrlService {

    public DomainURL addDomainUrl(String domainName, String domainUrl, long coolDownPeriod);
	
	public SimpleURL addSimpleUrl(String name, String url, String status, String domainName, String sourceDomainName);
    public void addSimpleUrls(List<String> urls, String status, String domainURL, String sourceDomainName);

    public void updateSimpleUrlStatus(String url, String status);
    public void updateSimpleUrlErrorStatus(String url, int errorDelta);

    Page<DomainURL> findDomains(Pageable pageable);

	Collection<SimpleURL> findURLs(String address,String status, int pageNo, int pageSize);
	Collection<SimpleURL> findExternalURLs(String address,String status, int pageNo, int pageSize);

	public void removeSimpleUrl(String simpleUrl) ;
	public void removeDomainUrl(String domainUrl) ;

	public void removeAllDomains();

    public List<Entries> query(String query);

}

package service;

import entity.DomainUrl;
import entity.SimpleUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UrlService {

	public DomainUrl addDomainUrl(String domainName, String domainUrl);
	
	public SimpleUrl addSimpleUrl(String name, String url, String domainName);

    Page<DomainUrl> findDomains(Pageable pageable);

	public void removeSimpleUrl(String simpleUrl) ;
	public void removeDomainUrl(String domainUrl) ;

	public void removeAllDomains();

}

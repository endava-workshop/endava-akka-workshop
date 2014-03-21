package service.impl;

import entity.DomainUrl;
import entity.SimpleUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.conversion.EndResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repo.DomainUrlRepo;
import repo.SimpleUrlRepo;
import service.UrlService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class UrlServiceImpl implements UrlService {

	@Autowired
	DomainUrlRepo domainRepo;
	
	@Autowired
	SimpleUrlRepo simpleUrlRepo;

	@Transactional
	public DomainUrl addDomainUrl(String domainName, String domainUrl) {
		DomainUrl domain = new DomainUrl(domainName, domainUrl);
		domain = domainRepo.save(domain);
		return domain;
	}

	@Transactional
	public SimpleUrl addSimpleUrl(String name, String url, String domainName) {
		DomainUrl domain = domainRepo.findByPropertyValue("name", domainName);
		if(domain == null){
			return null;
		}
		
		SimpleUrl simpleUrl = new SimpleUrl(name, url);
		domain.addInternalUrl(simpleUrl);
		simpleUrl = simpleUrlRepo.save(simpleUrl);
		domainRepo.save(domain);
		
		return simpleUrl;
	}

    @Transactional
    @Override
    public Page<DomainUrl> findDomains(Pageable pageable) {
        Page<DomainUrl> domains = domainRepo.findAll(pageable);
        return domains;
    }

	@Transactional
	@Override
	public Collection<SimpleUrl> findURLs(String address) {
		EndResult<DomainUrl> addresses = domainRepo.findAllByPropertyValue("name", address);
		if (addresses != null) {
			DomainUrl domainUrl = addresses.singleOrNull();
			Set<SimpleUrl> urls = new HashSet<>();
			if (domainUrl != null) {
				for (SimpleUrl simpleUrl : domainUrl.getInternalUrlSet()) {
					urls.add(simpleUrlRepo.findOne(simpleUrl.getId())); // TODO why do we need to re-hidrate?
				}
			}
			return urls;
		}
		return null;
	}

    @Transactional
	public void removeSimpleUrl(String name) {
		SimpleUrl simpleUrl = simpleUrlRepo.findByPropertyValue("name", name);
		if(simpleUrl != null){
			simpleUrlRepo.delete(simpleUrl);
		}

	}

	public void removeDomainUrl(String domainName) {
		DomainUrl domain = domainRepo.findByPropertyValue("name", domainName);
		
		for(SimpleUrl simpleUrl : domain.getInternalUrlSet()){
			simpleUrlRepo.delete(simpleUrl);
		}
		
		domainRepo.delete(domain);
	}

	@Transactional
	@Override
	public void removeAllDomains() {
		simpleUrlRepo.deleteAll();
		domainRepo.deleteAll();
	}

}

package service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import repo.DomainUrlRepo;
import service.UrlService;

public class UrlServiceImpl implements UrlService {

	@Autowired
	DomainUrlRepo urlRepo;

	public void addDomainUrl(String urlValue) {


	}

	public void addSimpleUrl(String urlValue, String domainUrl) {
		// TODO Auto-generated method stub

	}

	public void removeSimpleUrl(String simpleUrl) {
		// TODO Auto-generated method stub

	}

	public void removeDomainUrl(String domainUrl) {
		// TODO Auto-generated method stub

	}

}

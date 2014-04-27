package com.en_workshop.webcrawlerakka.dao.impl;

import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.dao.DomainDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.rest.DomainURLClient;
import java.util.List;


/**
 * Created by ionut on 20.04.2014.
 */
public class RestDomainDao implements DomainDao {

    public List<Domain> findAll() {
//        return DomainURLClient.listDomains(5, 1000/*WebCrawlerConstants.DOMAINS_CRAWL_MAX_COUNT*/);
        return DomainURLClient.listDomains(0, WebCrawlerConstants.DOMAINS_CRAWL_MAX_COUNT);
    }

}

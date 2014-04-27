package com.en_workshop.webcrawlerakka.dao.impl;

import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.dao.DomainDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.rest.DomainURLClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by ionut on 20.04.2014.
 */
public class RestDomainDao implements DomainDao {

    public List<Domain> findAll() {
        return DomainURLClient.listDomains(5, 1000/*WebCrawlerConstants.DOMAINS_CRAWL_MAX_COUNT*/);
    }

}

package com.en_workshop.webcrawlerakka.dao;

import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.enums.DomainStatus;

import java.util.List;

/**
 * Created by ionut on 20.04.2014.
 */
public interface DomainDao {

    //TODO update domain error count

    List<Domain> findAll();

    List<Domain> findAll(List<DomainStatus> domainStatuses);

    public void update(final Domain domain);

}

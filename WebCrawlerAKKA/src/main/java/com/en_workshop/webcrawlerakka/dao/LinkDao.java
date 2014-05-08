package com.en_workshop.webcrawlerakka.dao;

import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.DomainLink;
import com.en_workshop.webcrawlerakka.entities.Link;

import java.util.List;

/**
 * @author Radu Ciumag
 */
public interface LinkDao {

    public Link getNextForCrawling(final Domain domain);

    public void bulkCreate(final List<DomainLink> newDomainLinks);

    public void create(final DomainLink newDomainLink);

    public void update(final Link link);

}

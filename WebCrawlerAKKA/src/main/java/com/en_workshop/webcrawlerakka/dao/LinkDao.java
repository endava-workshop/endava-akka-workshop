package com.en_workshop.webcrawlerakka.dao;

import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;

/**
 * @author Radu Ciumag
 */
public interface LinkDao {

    public Link getNextForCrawling(final Domain domain);

    public void create(final Link newLink);

}

package com.en_workshop.webcrawlerakka.dao;

import com.en_workshop.webcrawlerakka.entities.Domain;

import java.util.List;

/**
 * Created by ionut on 20.04.2014.
 */
public interface DomainDao {

    //TODO update domain error count

    List<Domain> findAll();

}

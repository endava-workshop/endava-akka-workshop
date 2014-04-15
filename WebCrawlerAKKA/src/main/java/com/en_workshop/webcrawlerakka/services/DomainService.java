package com.en_workshop.webcrawlerakka.services;

import com.en_workshop.webcrawlerakka.entities.Domain;

import java.util.List;

/**
 * Service for the operations on {@link com.en_workshop.webcrawlerakka.entities.Domain}.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 */
public interface DomainService {

    /**
     * Retrieves the list of domains.
     * @return the list of domains.
     */
    public List<Domain> getDomains();

    /**
     * Saves or updates the specified {@link com.en_workshop.webcrawlerakka.entities.Domain}
     * @param domain the domain to be saved or updated.
     * @return the saved domain.
     */
    public Domain save(Domain domain);

}

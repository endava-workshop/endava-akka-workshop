package com.en_workshop.webcrawlerakka.services;

import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.entities.LinkContent;

/**
 * Service for the operations on {@link com.en_workshop.webcrawlerakka.entities.Link}.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 */
public interface LinkService {

    /**
     * Retrieves one link with the status NOT_VISITED for the specified domain.
     * @return one unvisited link.
     */
    public Link getUnvisitedLink(Domain domain);

    /**
     * Saves or updates the specified {@link com.en_workshop.webcrawlerakka.entities.Link}.
     * @param link the link to save or update.
     * @return the saved link.
     */
    public Link save(Link link);

    /**
     * Persists the content of a link.
     * @param linkContent container for the link and the content to be persisted.
     * @return {@code true} if the operation is successful, {@code false} otherwise.
     */
    public boolean persistContent(LinkContent linkContent);


}

package org.ssandwih.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ssandwih.domain.Locations;

/**
 * Service Interface for managing {@link org.ssandwih.domain.Locations}.
 */
public interface LocationsService {
    /**
     * Save a locations.
     *
     * @param locations the entity to save.
     * @return the persisted entity.
     */
    Locations save(Locations locations);

    /**
     * Updates a locations.
     *
     * @param locations the entity to update.
     * @return the persisted entity.
     */
    Locations update(Locations locations);

    /**
     * Partially updates a locations.
     *
     * @param locations the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Locations> partialUpdate(Locations locations);

    /**
     * Get all the locations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Locations> findAll(Pageable pageable);

    /**
     * Get all the locations with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Locations> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" locations.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Locations> findOne(Long id);

    /**
     * Delete the "id" locations.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the locations corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Locations> search(String query, Pageable pageable);
}

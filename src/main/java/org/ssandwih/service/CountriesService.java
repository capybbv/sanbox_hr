package org.ssandwih.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ssandwih.domain.Countries;

/**
 * Service Interface for managing {@link org.ssandwih.domain.Countries}.
 */
public interface CountriesService {
    /**
     * Save a countries.
     *
     * @param countries the entity to save.
     * @return the persisted entity.
     */
    Countries save(Countries countries);

    /**
     * Updates a countries.
     *
     * @param countries the entity to update.
     * @return the persisted entity.
     */
    Countries update(Countries countries);

    /**
     * Partially updates a countries.
     *
     * @param countries the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Countries> partialUpdate(Countries countries);

    /**
     * Get all the countries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Countries> findAll(Pageable pageable);

    /**
     * Get all the countries with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Countries> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" countries.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Countries> findOne(Long id);

    /**
     * Delete the "id" countries.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the countries corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Countries> search(String query, Pageable pageable);
}

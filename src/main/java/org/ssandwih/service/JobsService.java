package org.ssandwih.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ssandwih.domain.Jobs;

/**
 * Service Interface for managing {@link org.ssandwih.domain.Jobs}.
 */
public interface JobsService {
    /**
     * Save a jobs.
     *
     * @param jobs the entity to save.
     * @return the persisted entity.
     */
    Jobs save(Jobs jobs);

    /**
     * Updates a jobs.
     *
     * @param jobs the entity to update.
     * @return the persisted entity.
     */
    Jobs update(Jobs jobs);

    /**
     * Partially updates a jobs.
     *
     * @param jobs the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Jobs> partialUpdate(Jobs jobs);

    /**
     * Get all the jobs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Jobs> findAll(Pageable pageable);

    /**
     * Get the "id" jobs.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Jobs> findOne(Long id);

    /**
     * Delete the "id" jobs.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the jobs corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Jobs> search(String query, Pageable pageable);
}

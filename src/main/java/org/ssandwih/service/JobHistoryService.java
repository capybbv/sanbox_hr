package org.ssandwih.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ssandwih.domain.JobHistory;

/**
 * Service Interface for managing {@link org.ssandwih.domain.JobHistory}.
 */
public interface JobHistoryService {
    /**
     * Save a jobHistory.
     *
     * @param jobHistory the entity to save.
     * @return the persisted entity.
     */
    JobHistory save(JobHistory jobHistory);

    /**
     * Updates a jobHistory.
     *
     * @param jobHistory the entity to update.
     * @return the persisted entity.
     */
    JobHistory update(JobHistory jobHistory);

    /**
     * Partially updates a jobHistory.
     *
     * @param jobHistory the entity to update partially.
     * @return the persisted entity.
     */
    Optional<JobHistory> partialUpdate(JobHistory jobHistory);

    /**
     * Get all the jobHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<JobHistory> findAll(Pageable pageable);

    /**
     * Get all the jobHistories with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<JobHistory> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" jobHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<JobHistory> findOne(Long id);

    /**
     * Delete the "id" jobHistory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the jobHistory corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<JobHistory> search(String query, Pageable pageable);
}

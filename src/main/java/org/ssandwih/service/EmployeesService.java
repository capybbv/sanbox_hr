package org.ssandwih.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ssandwih.domain.Employees;

/**
 * Service Interface for managing {@link org.ssandwih.domain.Employees}.
 */
public interface EmployeesService {
    /**
     * Save a employees.
     *
     * @param employees the entity to save.
     * @return the persisted entity.
     */
    Employees save(Employees employees);

    /**
     * Updates a employees.
     *
     * @param employees the entity to update.
     * @return the persisted entity.
     */
    Employees update(Employees employees);

    /**
     * Partially updates a employees.
     *
     * @param employees the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Employees> partialUpdate(Employees employees);

    /**
     * Get all the employees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Employees> findAll(Pageable pageable);

    /**
     * Get all the employees with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Employees> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" employees.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Employees> findOne(Long id);

    /**
     * Delete the "id" employees.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the employees corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Employees> search(String query, Pageable pageable);
}

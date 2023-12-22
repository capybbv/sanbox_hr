package org.ssandwih.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssandwih.domain.Departments;
import org.ssandwih.repository.DepartmentsRepository;
import org.ssandwih.repository.search.DepartmentsSearchRepository;
import org.ssandwih.service.DepartmentsService;

/**
 * Service Implementation for managing {@link org.ssandwih.domain.Departments}.
 */
@Service
@Transactional
public class DepartmentsServiceImpl implements DepartmentsService {

    private final Logger log = LoggerFactory.getLogger(DepartmentsServiceImpl.class);

    private final DepartmentsRepository departmentsRepository;

    private final DepartmentsSearchRepository departmentsSearchRepository;

    public DepartmentsServiceImpl(DepartmentsRepository departmentsRepository, DepartmentsSearchRepository departmentsSearchRepository) {
        this.departmentsRepository = departmentsRepository;
        this.departmentsSearchRepository = departmentsSearchRepository;
    }

    @Override
    public Departments save(Departments departments) {
        log.debug("Request to save Departments : {}", departments);
        Departments result = departmentsRepository.save(departments);
        departmentsSearchRepository.index(result);
        return result;
    }

    @Override
    public Departments update(Departments departments) {
        log.debug("Request to update Departments : {}", departments);
        Departments result = departmentsRepository.save(departments);
        departmentsSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Departments> partialUpdate(Departments departments) {
        log.debug("Request to partially update Departments : {}", departments);

        return departmentsRepository
            .findById(departments.getId())
            .map(existingDepartments -> {
                if (departments.getDepartmentId() != null) {
                    existingDepartments.setDepartmentId(departments.getDepartmentId());
                }
                if (departments.getDepartmentName() != null) {
                    existingDepartments.setDepartmentName(departments.getDepartmentName());
                }

                return existingDepartments;
            })
            .map(departmentsRepository::save)
            .map(savedDepartments -> {
                departmentsSearchRepository.index(savedDepartments);
                return savedDepartments;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Departments> findAll(Pageable pageable) {
        log.debug("Request to get all Departments");
        return departmentsRepository.findAll(pageable);
    }

    public Page<Departments> findAllWithEagerRelationships(Pageable pageable) {
        return departmentsRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Departments> findOne(Long id) {
        log.debug("Request to get Departments : {}", id);
        return departmentsRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Departments : {}", id);
        departmentsRepository.deleteById(id);
        departmentsSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Departments> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Departments for query {}", query);
        return departmentsSearchRepository.search(query, pageable);
    }
}

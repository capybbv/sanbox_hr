package org.ssandwih.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssandwih.domain.Employees;
import org.ssandwih.repository.EmployeesRepository;
import org.ssandwih.repository.search.EmployeesSearchRepository;
import org.ssandwih.service.EmployeesService;

/**
 * Service Implementation for managing {@link org.ssandwih.domain.Employees}.
 */
@Service
@Transactional
public class EmployeesServiceImpl implements EmployeesService {

    private final Logger log = LoggerFactory.getLogger(EmployeesServiceImpl.class);

    private final EmployeesRepository employeesRepository;

    private final EmployeesSearchRepository employeesSearchRepository;

    public EmployeesServiceImpl(EmployeesRepository employeesRepository, EmployeesSearchRepository employeesSearchRepository) {
        this.employeesRepository = employeesRepository;
        this.employeesSearchRepository = employeesSearchRepository;
    }

    @Override
    public Employees save(Employees employees) {
        log.debug("Request to save Employees : {}", employees);
        Employees result = employeesRepository.save(employees);
        employeesSearchRepository.index(result);
        return result;
    }

    @Override
    public Employees update(Employees employees) {
        log.debug("Request to update Employees : {}", employees);
        Employees result = employeesRepository.save(employees);
        employeesSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Employees> partialUpdate(Employees employees) {
        log.debug("Request to partially update Employees : {}", employees);

        return employeesRepository
            .findById(employees.getId())
            .map(existingEmployees -> {
                if (employees.getEmployeeId() != null) {
                    existingEmployees.setEmployeeId(employees.getEmployeeId());
                }
                if (employees.getFirstName() != null) {
                    existingEmployees.setFirstName(employees.getFirstName());
                }
                if (employees.getLastName() != null) {
                    existingEmployees.setLastName(employees.getLastName());
                }
                if (employees.getEmail() != null) {
                    existingEmployees.setEmail(employees.getEmail());
                }
                if (employees.getPhoneNumber() != null) {
                    existingEmployees.setPhoneNumber(employees.getPhoneNumber());
                }
                if (employees.getHireDate() != null) {
                    existingEmployees.setHireDate(employees.getHireDate());
                }
                if (employees.getSalary() != null) {
                    existingEmployees.setSalary(employees.getSalary());
                }
                if (employees.getCommissionPct() != null) {
                    existingEmployees.setCommissionPct(employees.getCommissionPct());
                }

                return existingEmployees;
            })
            .map(employeesRepository::save)
            .map(savedEmployees -> {
                employeesSearchRepository.index(savedEmployees);
                return savedEmployees;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Employees> findAll(Pageable pageable) {
        log.debug("Request to get all Employees");
        return employeesRepository.findAll(pageable);
    }

    public Page<Employees> findAllWithEagerRelationships(Pageable pageable) {
        return employeesRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employees> findOne(Long id) {
        log.debug("Request to get Employees : {}", id);
        return employeesRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Employees : {}", id);
        employeesRepository.deleteById(id);
        employeesSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Employees> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Employees for query {}", query);
        return employeesSearchRepository.search(query, pageable);
    }
}

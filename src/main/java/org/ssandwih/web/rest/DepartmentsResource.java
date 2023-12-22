package org.ssandwih.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.ssandwih.domain.Departments;
import org.ssandwih.repository.DepartmentsRepository;
import org.ssandwih.service.DepartmentsService;
import org.ssandwih.web.rest.errors.BadRequestAlertException;
import org.ssandwih.web.rest.errors.ElasticsearchExceptionMapper;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.ssandwih.domain.Departments}.
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentsResource {

    private final Logger log = LoggerFactory.getLogger(DepartmentsResource.class);

    private static final String ENTITY_NAME = "departments";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DepartmentsService departmentsService;

    private final DepartmentsRepository departmentsRepository;

    public DepartmentsResource(DepartmentsService departmentsService, DepartmentsRepository departmentsRepository) {
        this.departmentsService = departmentsService;
        this.departmentsRepository = departmentsRepository;
    }

    /**
     * {@code POST  /departments} : Create a new departments.
     *
     * @param departments the departments to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new departments, or with status {@code 400 (Bad Request)} if the departments has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Departments> createDepartments(@Valid @RequestBody Departments departments) throws URISyntaxException {
        log.debug("REST request to save Departments : {}", departments);
        if (departments.getId() != null) {
            throw new BadRequestAlertException("A new departments cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Departments result = departmentsService.save(departments);
        return ResponseEntity
            .created(new URI("/api/departments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /departments/:id} : Updates an existing departments.
     *
     * @param id the id of the departments to save.
     * @param departments the departments to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated departments,
     * or with status {@code 400 (Bad Request)} if the departments is not valid,
     * or with status {@code 500 (Internal Server Error)} if the departments couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Departments> updateDepartments(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Departments departments
    ) throws URISyntaxException {
        log.debug("REST request to update Departments : {}, {}", id, departments);
        if (departments.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, departments.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!departmentsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Departments result = departmentsService.update(departments);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, departments.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /departments/:id} : Partial updates given fields of an existing departments, field will ignore if it is null
     *
     * @param id the id of the departments to save.
     * @param departments the departments to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated departments,
     * or with status {@code 400 (Bad Request)} if the departments is not valid,
     * or with status {@code 404 (Not Found)} if the departments is not found,
     * or with status {@code 500 (Internal Server Error)} if the departments couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Departments> partialUpdateDepartments(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Departments departments
    ) throws URISyntaxException {
        log.debug("REST request to partial update Departments partially : {}, {}", id, departments);
        if (departments.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, departments.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!departmentsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Departments> result = departmentsService.partialUpdate(departments);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, departments.getId().toString())
        );
    }

    /**
     * {@code GET  /departments} : get all the departments.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of departments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Departments>> getAllDepartments(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Departments");
        Page<Departments> page;
        if (eagerload) {
            page = departmentsService.findAllWithEagerRelationships(pageable);
        } else {
            page = departmentsService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /departments/:id} : get the "id" departments.
     *
     * @param id the id of the departments to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the departments, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Departments> getDepartments(@PathVariable("id") Long id) {
        log.debug("REST request to get Departments : {}", id);
        Optional<Departments> departments = departmentsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(departments);
    }

    /**
     * {@code DELETE  /departments/:id} : delete the "id" departments.
     *
     * @param id the id of the departments to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartments(@PathVariable("id") Long id) {
        log.debug("REST request to delete Departments : {}", id);
        departmentsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /departments/_search?query=:query} : search for the departments corresponding
     * to the query.
     *
     * @param query the query of the departments search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<Departments>> searchDepartments(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Departments for query {}", query);
        try {
            Page<Departments> page = departmentsService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

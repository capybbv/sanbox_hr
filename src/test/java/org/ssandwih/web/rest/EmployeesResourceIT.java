package org.ssandwih.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.ssandwih.web.rest.TestUtil.sameInstant;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.ssandwih.IntegrationTest;
import org.ssandwih.domain.Employees;
import org.ssandwih.repository.EmployeesRepository;
import org.ssandwih.repository.search.EmployeesSearchRepository;
import org.ssandwih.service.EmployeesService;

/**
 * Integration tests for the {@link EmployeesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EmployeesResourceIT {

    private static final Integer DEFAULT_EMPLOYEE_ID = 1;
    private static final Integer UPDATED_EMPLOYEE_ID = 2;

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_HIRE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_HIRE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_SALARY = 1;
    private static final Integer UPDATED_SALARY = 2;

    private static final Integer DEFAULT_COMMISSION_PCT = 1;
    private static final Integer UPDATED_COMMISSION_PCT = 2;

    private static final String ENTITY_API_URL = "/api/employees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/employees/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EmployeesRepository employeesRepository;

    @Mock
    private EmployeesRepository employeesRepositoryMock;

    @Mock
    private EmployeesService employeesServiceMock;

    @Autowired
    private EmployeesSearchRepository employeesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmployeesMockMvc;

    private Employees employees;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employees createEntity(EntityManager em) {
        Employees employees = new Employees()
            .employeeId(DEFAULT_EMPLOYEE_ID)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .hireDate(DEFAULT_HIRE_DATE)
            .salary(DEFAULT_SALARY)
            .commissionPct(DEFAULT_COMMISSION_PCT);
        return employees;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employees createUpdatedEntity(EntityManager em) {
        Employees employees = new Employees()
            .employeeId(UPDATED_EMPLOYEE_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .hireDate(UPDATED_HIRE_DATE)
            .salary(UPDATED_SALARY)
            .commissionPct(UPDATED_COMMISSION_PCT);
        return employees;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        employeesSearchRepository.deleteAll();
        assertThat(employeesSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        employees = createEntity(em);
    }

    @Test
    @Transactional
    void createEmployees() throws Exception {
        int databaseSizeBeforeCreate = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        // Create the Employees
        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isCreated());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Employees testEmployees = employeesList.get(employeesList.size() - 1);
        assertThat(testEmployees.getEmployeeId()).isEqualTo(DEFAULT_EMPLOYEE_ID);
        assertThat(testEmployees.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testEmployees.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testEmployees.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testEmployees.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testEmployees.getHireDate()).isEqualTo(DEFAULT_HIRE_DATE);
        assertThat(testEmployees.getSalary()).isEqualTo(DEFAULT_SALARY);
        assertThat(testEmployees.getCommissionPct()).isEqualTo(DEFAULT_COMMISSION_PCT);
    }

    @Test
    @Transactional
    void createEmployeesWithExistingId() throws Exception {
        // Create the Employees with an existing ID
        employees.setId(1L);

        int databaseSizeBeforeCreate = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isBadRequest());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmployeeIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        // set the field null
        employees.setEmployeeId(null);

        // Create the Employees, which fails.

        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isBadRequest());

        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        // set the field null
        employees.setLastName(null);

        // Create the Employees, which fails.

        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isBadRequest());

        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        // set the field null
        employees.setEmail(null);

        // Create the Employees, which fails.

        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isBadRequest());

        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkHireDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        // set the field null
        employees.setHireDate(null);

        // Create the Employees, which fails.

        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isBadRequest());

        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEmployees() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList
        restEmployeesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employees.getId().intValue())))
            .andExpect(jsonPath("$.[*].employeeId").value(hasItem(DEFAULT_EMPLOYEE_ID)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].hireDate").value(hasItem(sameInstant(DEFAULT_HIRE_DATE))))
            .andExpect(jsonPath("$.[*].salary").value(hasItem(DEFAULT_SALARY)))
            .andExpect(jsonPath("$.[*].commissionPct").value(hasItem(DEFAULT_COMMISSION_PCT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmployeesWithEagerRelationshipsIsEnabled() throws Exception {
        when(employeesServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEmployeesMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(employeesServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmployeesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(employeesServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEmployeesMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(employeesRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEmployees() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get the employees
        restEmployeesMockMvc
            .perform(get(ENTITY_API_URL_ID, employees.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(employees.getId().intValue()))
            .andExpect(jsonPath("$.employeeId").value(DEFAULT_EMPLOYEE_ID))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.hireDate").value(sameInstant(DEFAULT_HIRE_DATE)))
            .andExpect(jsonPath("$.salary").value(DEFAULT_SALARY))
            .andExpect(jsonPath("$.commissionPct").value(DEFAULT_COMMISSION_PCT));
    }

    @Test
    @Transactional
    void getNonExistingEmployees() throws Exception {
        // Get the employees
        restEmployeesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEmployees() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        employeesSearchRepository.save(employees);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());

        // Update the employees
        Employees updatedEmployees = employeesRepository.findById(employees.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEmployees are not directly saved in db
        em.detach(updatedEmployees);
        updatedEmployees
            .employeeId(UPDATED_EMPLOYEE_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .hireDate(UPDATED_HIRE_DATE)
            .salary(UPDATED_SALARY)
            .commissionPct(UPDATED_COMMISSION_PCT);

        restEmployeesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEmployees.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEmployees))
            )
            .andExpect(status().isOk());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        Employees testEmployees = employeesList.get(employeesList.size() - 1);
        assertThat(testEmployees.getEmployeeId()).isEqualTo(UPDATED_EMPLOYEE_ID);
        assertThat(testEmployees.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testEmployees.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testEmployees.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEmployees.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testEmployees.getHireDate()).isEqualTo(UPDATED_HIRE_DATE);
        assertThat(testEmployees.getSalary()).isEqualTo(UPDATED_SALARY);
        assertThat(testEmployees.getCommissionPct()).isEqualTo(UPDATED_COMMISSION_PCT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Employees> employeesSearchList = IterableUtils.toList(employeesSearchRepository.findAll());
                Employees testEmployeesSearch = employeesSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEmployeesSearch.getEmployeeId()).isEqualTo(UPDATED_EMPLOYEE_ID);
                assertThat(testEmployeesSearch.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
                assertThat(testEmployeesSearch.getLastName()).isEqualTo(UPDATED_LAST_NAME);
                assertThat(testEmployeesSearch.getEmail()).isEqualTo(UPDATED_EMAIL);
                assertThat(testEmployeesSearch.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
                assertThat(testEmployeesSearch.getHireDate()).isEqualTo(UPDATED_HIRE_DATE);
                assertThat(testEmployeesSearch.getSalary()).isEqualTo(UPDATED_SALARY);
                assertThat(testEmployeesSearch.getCommissionPct()).isEqualTo(UPDATED_COMMISSION_PCT);
            });
    }

    @Test
    @Transactional
    void putNonExistingEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        employees.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, employees.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employees))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        employees.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employees))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        employees.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEmployeesWithPatch() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();

        // Update the employees using partial update
        Employees partialUpdatedEmployees = new Employees();
        partialUpdatedEmployees.setId(employees.getId());

        partialUpdatedEmployees
            .employeeId(UPDATED_EMPLOYEE_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .salary(UPDATED_SALARY)
            .commissionPct(UPDATED_COMMISSION_PCT);

        restEmployeesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployees.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmployees))
            )
            .andExpect(status().isOk());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        Employees testEmployees = employeesList.get(employeesList.size() - 1);
        assertThat(testEmployees.getEmployeeId()).isEqualTo(UPDATED_EMPLOYEE_ID);
        assertThat(testEmployees.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testEmployees.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testEmployees.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEmployees.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testEmployees.getHireDate()).isEqualTo(DEFAULT_HIRE_DATE);
        assertThat(testEmployees.getSalary()).isEqualTo(UPDATED_SALARY);
        assertThat(testEmployees.getCommissionPct()).isEqualTo(UPDATED_COMMISSION_PCT);
    }

    @Test
    @Transactional
    void fullUpdateEmployeesWithPatch() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();

        // Update the employees using partial update
        Employees partialUpdatedEmployees = new Employees();
        partialUpdatedEmployees.setId(employees.getId());

        partialUpdatedEmployees
            .employeeId(UPDATED_EMPLOYEE_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .hireDate(UPDATED_HIRE_DATE)
            .salary(UPDATED_SALARY)
            .commissionPct(UPDATED_COMMISSION_PCT);

        restEmployeesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployees.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmployees))
            )
            .andExpect(status().isOk());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        Employees testEmployees = employeesList.get(employeesList.size() - 1);
        assertThat(testEmployees.getEmployeeId()).isEqualTo(UPDATED_EMPLOYEE_ID);
        assertThat(testEmployees.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testEmployees.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testEmployees.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEmployees.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testEmployees.getHireDate()).isEqualTo(UPDATED_HIRE_DATE);
        assertThat(testEmployees.getSalary()).isEqualTo(UPDATED_SALARY);
        assertThat(testEmployees.getCommissionPct()).isEqualTo(UPDATED_COMMISSION_PCT);
    }

    @Test
    @Transactional
    void patchNonExistingEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        employees.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, employees.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employees))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        employees.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employees))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        employees.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(employees))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEmployees() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);
        employeesRepository.save(employees);
        employeesSearchRepository.save(employees);

        int databaseSizeBeforeDelete = employeesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the employees
        restEmployeesMockMvc
            .perform(delete(ENTITY_API_URL_ID, employees.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEmployees() throws Exception {
        // Initialize the database
        employees = employeesRepository.saveAndFlush(employees);
        employeesSearchRepository.save(employees);

        // Search the employees
        restEmployeesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + employees.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employees.getId().intValue())))
            .andExpect(jsonPath("$.[*].employeeId").value(hasItem(DEFAULT_EMPLOYEE_ID)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].hireDate").value(hasItem(sameInstant(DEFAULT_HIRE_DATE))))
            .andExpect(jsonPath("$.[*].salary").value(hasItem(DEFAULT_SALARY)))
            .andExpect(jsonPath("$.[*].commissionPct").value(hasItem(DEFAULT_COMMISSION_PCT)));
    }
}

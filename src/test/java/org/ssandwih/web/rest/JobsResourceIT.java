package org.ssandwih.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.ssandwih.IntegrationTest;
import org.ssandwih.domain.Jobs;
import org.ssandwih.repository.JobsRepository;
import org.ssandwih.repository.search.JobsSearchRepository;

/**
 * Integration tests for the {@link JobsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JobsResourceIT {

    private static final Integer DEFAULT_JOB_ID = 1;
    private static final Integer UPDATED_JOB_ID = 2;

    private static final String DEFAULT_JOB_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_JOB_TITLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_MIN_SALARY = 1;
    private static final Integer UPDATED_MIN_SALARY = 2;

    private static final Integer DEFAULT_MAX_SALARY = 1;
    private static final Integer UPDATED_MAX_SALARY = 2;

    private static final String ENTITY_API_URL = "/api/jobs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/jobs/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private JobsRepository jobsRepository;

    @Autowired
    private JobsSearchRepository jobsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJobsMockMvc;

    private Jobs jobs;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jobs createEntity(EntityManager em) {
        Jobs jobs = new Jobs()
            .jobId(DEFAULT_JOB_ID)
            .jobTitle(DEFAULT_JOB_TITLE)
            .minSalary(DEFAULT_MIN_SALARY)
            .maxSalary(DEFAULT_MAX_SALARY);
        return jobs;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jobs createUpdatedEntity(EntityManager em) {
        Jobs jobs = new Jobs()
            .jobId(UPDATED_JOB_ID)
            .jobTitle(UPDATED_JOB_TITLE)
            .minSalary(UPDATED_MIN_SALARY)
            .maxSalary(UPDATED_MAX_SALARY);
        return jobs;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        jobsSearchRepository.deleteAll();
        assertThat(jobsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        jobs = createEntity(em);
    }

    @Test
    @Transactional
    void createJobs() throws Exception {
        int databaseSizeBeforeCreate = jobsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        // Create the Jobs
        restJobsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobs)))
            .andExpect(status().isCreated());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Jobs testJobs = jobsList.get(jobsList.size() - 1);
        assertThat(testJobs.getJobId()).isEqualTo(DEFAULT_JOB_ID);
        assertThat(testJobs.getJobTitle()).isEqualTo(DEFAULT_JOB_TITLE);
        assertThat(testJobs.getMinSalary()).isEqualTo(DEFAULT_MIN_SALARY);
        assertThat(testJobs.getMaxSalary()).isEqualTo(DEFAULT_MAX_SALARY);
    }

    @Test
    @Transactional
    void createJobsWithExistingId() throws Exception {
        // Create the Jobs with an existing ID
        jobs.setId(1L);

        int databaseSizeBeforeCreate = jobsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobs)))
            .andExpect(status().isBadRequest());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkJobIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        // set the field null
        jobs.setJobId(null);

        // Create the Jobs, which fails.

        restJobsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobs)))
            .andExpect(status().isBadRequest());

        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkJobTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        // set the field null
        jobs.setJobTitle(null);

        // Create the Jobs, which fails.

        restJobsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobs)))
            .andExpect(status().isBadRequest());

        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllJobs() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList
        restJobsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobs.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobId").value(hasItem(DEFAULT_JOB_ID)))
            .andExpect(jsonPath("$.[*].jobTitle").value(hasItem(DEFAULT_JOB_TITLE)))
            .andExpect(jsonPath("$.[*].minSalary").value(hasItem(DEFAULT_MIN_SALARY)))
            .andExpect(jsonPath("$.[*].maxSalary").value(hasItem(DEFAULT_MAX_SALARY)));
    }

    @Test
    @Transactional
    void getJobs() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get the jobs
        restJobsMockMvc
            .perform(get(ENTITY_API_URL_ID, jobs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(jobs.getId().intValue()))
            .andExpect(jsonPath("$.jobId").value(DEFAULT_JOB_ID))
            .andExpect(jsonPath("$.jobTitle").value(DEFAULT_JOB_TITLE))
            .andExpect(jsonPath("$.minSalary").value(DEFAULT_MIN_SALARY))
            .andExpect(jsonPath("$.maxSalary").value(DEFAULT_MAX_SALARY));
    }

    @Test
    @Transactional
    void getNonExistingJobs() throws Exception {
        // Get the jobs
        restJobsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingJobs() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        jobsSearchRepository.save(jobs);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());

        // Update the jobs
        Jobs updatedJobs = jobsRepository.findById(jobs.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedJobs are not directly saved in db
        em.detach(updatedJobs);
        updatedJobs.jobId(UPDATED_JOB_ID).jobTitle(UPDATED_JOB_TITLE).minSalary(UPDATED_MIN_SALARY).maxSalary(UPDATED_MAX_SALARY);

        restJobsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedJobs.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedJobs))
            )
            .andExpect(status().isOk());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        Jobs testJobs = jobsList.get(jobsList.size() - 1);
        assertThat(testJobs.getJobId()).isEqualTo(UPDATED_JOB_ID);
        assertThat(testJobs.getJobTitle()).isEqualTo(UPDATED_JOB_TITLE);
        assertThat(testJobs.getMinSalary()).isEqualTo(UPDATED_MIN_SALARY);
        assertThat(testJobs.getMaxSalary()).isEqualTo(UPDATED_MAX_SALARY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Jobs> jobsSearchList = IterableUtils.toList(jobsSearchRepository.findAll());
                Jobs testJobsSearch = jobsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testJobsSearch.getJobId()).isEqualTo(UPDATED_JOB_ID);
                assertThat(testJobsSearch.getJobTitle()).isEqualTo(UPDATED_JOB_TITLE);
                assertThat(testJobsSearch.getMinSalary()).isEqualTo(UPDATED_MIN_SALARY);
                assertThat(testJobsSearch.getMaxSalary()).isEqualTo(UPDATED_MAX_SALARY);
            });
    }

    @Test
    @Transactional
    void putNonExistingJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        jobs.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jobs.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        jobs.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        jobs.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobs)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateJobsWithPatch() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();

        // Update the jobs using partial update
        Jobs partialUpdatedJobs = new Jobs();
        partialUpdatedJobs.setId(jobs.getId());

        partialUpdatedJobs.jobTitle(UPDATED_JOB_TITLE).minSalary(UPDATED_MIN_SALARY);

        restJobsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJobs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJobs))
            )
            .andExpect(status().isOk());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        Jobs testJobs = jobsList.get(jobsList.size() - 1);
        assertThat(testJobs.getJobId()).isEqualTo(DEFAULT_JOB_ID);
        assertThat(testJobs.getJobTitle()).isEqualTo(UPDATED_JOB_TITLE);
        assertThat(testJobs.getMinSalary()).isEqualTo(UPDATED_MIN_SALARY);
        assertThat(testJobs.getMaxSalary()).isEqualTo(DEFAULT_MAX_SALARY);
    }

    @Test
    @Transactional
    void fullUpdateJobsWithPatch() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();

        // Update the jobs using partial update
        Jobs partialUpdatedJobs = new Jobs();
        partialUpdatedJobs.setId(jobs.getId());

        partialUpdatedJobs.jobId(UPDATED_JOB_ID).jobTitle(UPDATED_JOB_TITLE).minSalary(UPDATED_MIN_SALARY).maxSalary(UPDATED_MAX_SALARY);

        restJobsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJobs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJobs))
            )
            .andExpect(status().isOk());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        Jobs testJobs = jobsList.get(jobsList.size() - 1);
        assertThat(testJobs.getJobId()).isEqualTo(UPDATED_JOB_ID);
        assertThat(testJobs.getJobTitle()).isEqualTo(UPDATED_JOB_TITLE);
        assertThat(testJobs.getMinSalary()).isEqualTo(UPDATED_MIN_SALARY);
        assertThat(testJobs.getMaxSalary()).isEqualTo(UPDATED_MAX_SALARY);
    }

    @Test
    @Transactional
    void patchNonExistingJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        jobs.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, jobs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        jobs.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        jobs.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(jobs)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteJobs() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);
        jobsRepository.save(jobs);
        jobsSearchRepository.save(jobs);

        int databaseSizeBeforeDelete = jobsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the jobs
        restJobsMockMvc
            .perform(delete(ENTITY_API_URL_ID, jobs.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchJobs() throws Exception {
        // Initialize the database
        jobs = jobsRepository.saveAndFlush(jobs);
        jobsSearchRepository.save(jobs);

        // Search the jobs
        restJobsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + jobs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobs.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobId").value(hasItem(DEFAULT_JOB_ID)))
            .andExpect(jsonPath("$.[*].jobTitle").value(hasItem(DEFAULT_JOB_TITLE)))
            .andExpect(jsonPath("$.[*].minSalary").value(hasItem(DEFAULT_MIN_SALARY)))
            .andExpect(jsonPath("$.[*].maxSalary").value(hasItem(DEFAULT_MAX_SALARY)));
    }
}

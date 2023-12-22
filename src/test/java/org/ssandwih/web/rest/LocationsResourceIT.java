package org.ssandwih.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
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
import org.ssandwih.domain.Locations;
import org.ssandwih.repository.LocationsRepository;
import org.ssandwih.repository.search.LocationsSearchRepository;
import org.ssandwih.service.LocationsService;

/**
 * Integration tests for the {@link LocationsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LocationsResourceIT {

    private static final Integer DEFAULT_LOCATION_ID = 1;
    private static final Integer UPDATED_LOCATION_ID = 2;

    private static final String DEFAULT_STREET_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_STREET_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_CODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTAL_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE_PROVINCE = "AAAAAAAAAA";
    private static final String UPDATED_STATE_PROVINCE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/locations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/locations/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LocationsRepository locationsRepository;

    @Mock
    private LocationsRepository locationsRepositoryMock;

    @Mock
    private LocationsService locationsServiceMock;

    @Autowired
    private LocationsSearchRepository locationsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLocationsMockMvc;

    private Locations locations;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Locations createEntity(EntityManager em) {
        Locations locations = new Locations()
            .locationId(DEFAULT_LOCATION_ID)
            .streetAddress(DEFAULT_STREET_ADDRESS)
            .postalCode(DEFAULT_POSTAL_CODE)
            .city(DEFAULT_CITY)
            .stateProvince(DEFAULT_STATE_PROVINCE);
        return locations;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Locations createUpdatedEntity(EntityManager em) {
        Locations locations = new Locations()
            .locationId(UPDATED_LOCATION_ID)
            .streetAddress(UPDATED_STREET_ADDRESS)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .stateProvince(UPDATED_STATE_PROVINCE);
        return locations;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        locationsSearchRepository.deleteAll();
        assertThat(locationsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        locations = createEntity(em);
    }

    @Test
    @Transactional
    void createLocations() throws Exception {
        int databaseSizeBeforeCreate = locationsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        // Create the Locations
        restLocationsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(locations)))
            .andExpect(status().isCreated());

        // Validate the Locations in the database
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(locationsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Locations testLocations = locationsList.get(locationsList.size() - 1);
        assertThat(testLocations.getLocationId()).isEqualTo(DEFAULT_LOCATION_ID);
        assertThat(testLocations.getStreetAddress()).isEqualTo(DEFAULT_STREET_ADDRESS);
        assertThat(testLocations.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testLocations.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testLocations.getStateProvince()).isEqualTo(DEFAULT_STATE_PROVINCE);
    }

    @Test
    @Transactional
    void createLocationsWithExistingId() throws Exception {
        // Create the Locations with an existing ID
        locations.setId(1L);

        int databaseSizeBeforeCreate = locationsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(locationsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocationsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(locations)))
            .andExpect(status().isBadRequest());

        // Validate the Locations in the database
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLocationIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = locationsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        // set the field null
        locations.setLocationId(null);

        // Create the Locations, which fails.

        restLocationsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(locations)))
            .andExpect(status().isBadRequest());

        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllLocations() throws Exception {
        // Initialize the database
        locationsRepository.saveAndFlush(locations);

        // Get all the locationsList
        restLocationsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locations.getId().intValue())))
            .andExpect(jsonPath("$.[*].locationId").value(hasItem(DEFAULT_LOCATION_ID)))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].stateProvince").value(hasItem(DEFAULT_STATE_PROVINCE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLocationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(locationsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLocationsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(locationsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLocationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(locationsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLocationsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(locationsRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLocations() throws Exception {
        // Initialize the database
        locationsRepository.saveAndFlush(locations);

        // Get the locations
        restLocationsMockMvc
            .perform(get(ENTITY_API_URL_ID, locations.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(locations.getId().intValue()))
            .andExpect(jsonPath("$.locationId").value(DEFAULT_LOCATION_ID))
            .andExpect(jsonPath("$.streetAddress").value(DEFAULT_STREET_ADDRESS))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.stateProvince").value(DEFAULT_STATE_PROVINCE));
    }

    @Test
    @Transactional
    void getNonExistingLocations() throws Exception {
        // Get the locations
        restLocationsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLocations() throws Exception {
        // Initialize the database
        locationsRepository.saveAndFlush(locations);

        int databaseSizeBeforeUpdate = locationsRepository.findAll().size();
        locationsSearchRepository.save(locations);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(locationsSearchRepository.findAll());

        // Update the locations
        Locations updatedLocations = locationsRepository.findById(locations.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLocations are not directly saved in db
        em.detach(updatedLocations);
        updatedLocations
            .locationId(UPDATED_LOCATION_ID)
            .streetAddress(UPDATED_STREET_ADDRESS)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .stateProvince(UPDATED_STATE_PROVINCE);

        restLocationsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLocations.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLocations))
            )
            .andExpect(status().isOk());

        // Validate the Locations in the database
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeUpdate);
        Locations testLocations = locationsList.get(locationsList.size() - 1);
        assertThat(testLocations.getLocationId()).isEqualTo(UPDATED_LOCATION_ID);
        assertThat(testLocations.getStreetAddress()).isEqualTo(UPDATED_STREET_ADDRESS);
        assertThat(testLocations.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testLocations.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testLocations.getStateProvince()).isEqualTo(UPDATED_STATE_PROVINCE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(locationsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Locations> locationsSearchList = IterableUtils.toList(locationsSearchRepository.findAll());
                Locations testLocationsSearch = locationsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testLocationsSearch.getLocationId()).isEqualTo(UPDATED_LOCATION_ID);
                assertThat(testLocationsSearch.getStreetAddress()).isEqualTo(UPDATED_STREET_ADDRESS);
                assertThat(testLocationsSearch.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
                assertThat(testLocationsSearch.getCity()).isEqualTo(UPDATED_CITY);
                assertThat(testLocationsSearch.getStateProvince()).isEqualTo(UPDATED_STATE_PROVINCE);
            });
    }

    @Test
    @Transactional
    void putNonExistingLocations() throws Exception {
        int databaseSizeBeforeUpdate = locationsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        locations.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocationsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, locations.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(locations))
            )
            .andExpect(status().isBadRequest());

        // Validate the Locations in the database
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchLocations() throws Exception {
        int databaseSizeBeforeUpdate = locationsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        locations.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(locations))
            )
            .andExpect(status().isBadRequest());

        // Validate the Locations in the database
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLocations() throws Exception {
        int databaseSizeBeforeUpdate = locationsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        locations.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(locations)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Locations in the database
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateLocationsWithPatch() throws Exception {
        // Initialize the database
        locationsRepository.saveAndFlush(locations);

        int databaseSizeBeforeUpdate = locationsRepository.findAll().size();

        // Update the locations using partial update
        Locations partialUpdatedLocations = new Locations();
        partialUpdatedLocations.setId(locations.getId());

        partialUpdatedLocations.streetAddress(UPDATED_STREET_ADDRESS);

        restLocationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocations.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLocations))
            )
            .andExpect(status().isOk());

        // Validate the Locations in the database
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeUpdate);
        Locations testLocations = locationsList.get(locationsList.size() - 1);
        assertThat(testLocations.getLocationId()).isEqualTo(DEFAULT_LOCATION_ID);
        assertThat(testLocations.getStreetAddress()).isEqualTo(UPDATED_STREET_ADDRESS);
        assertThat(testLocations.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testLocations.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testLocations.getStateProvince()).isEqualTo(DEFAULT_STATE_PROVINCE);
    }

    @Test
    @Transactional
    void fullUpdateLocationsWithPatch() throws Exception {
        // Initialize the database
        locationsRepository.saveAndFlush(locations);

        int databaseSizeBeforeUpdate = locationsRepository.findAll().size();

        // Update the locations using partial update
        Locations partialUpdatedLocations = new Locations();
        partialUpdatedLocations.setId(locations.getId());

        partialUpdatedLocations
            .locationId(UPDATED_LOCATION_ID)
            .streetAddress(UPDATED_STREET_ADDRESS)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .stateProvince(UPDATED_STATE_PROVINCE);

        restLocationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocations.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLocations))
            )
            .andExpect(status().isOk());

        // Validate the Locations in the database
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeUpdate);
        Locations testLocations = locationsList.get(locationsList.size() - 1);
        assertThat(testLocations.getLocationId()).isEqualTo(UPDATED_LOCATION_ID);
        assertThat(testLocations.getStreetAddress()).isEqualTo(UPDATED_STREET_ADDRESS);
        assertThat(testLocations.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testLocations.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testLocations.getStateProvince()).isEqualTo(UPDATED_STATE_PROVINCE);
    }

    @Test
    @Transactional
    void patchNonExistingLocations() throws Exception {
        int databaseSizeBeforeUpdate = locationsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        locations.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, locations.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(locations))
            )
            .andExpect(status().isBadRequest());

        // Validate the Locations in the database
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLocations() throws Exception {
        int databaseSizeBeforeUpdate = locationsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        locations.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(locations))
            )
            .andExpect(status().isBadRequest());

        // Validate the Locations in the database
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLocations() throws Exception {
        int databaseSizeBeforeUpdate = locationsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        locations.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocationsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(locations))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Locations in the database
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteLocations() throws Exception {
        // Initialize the database
        locationsRepository.saveAndFlush(locations);
        locationsRepository.save(locations);
        locationsSearchRepository.save(locations);

        int databaseSizeBeforeDelete = locationsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the locations
        restLocationsMockMvc
            .perform(delete(ENTITY_API_URL_ID, locations.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Locations> locationsList = locationsRepository.findAll();
        assertThat(locationsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(locationsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchLocations() throws Exception {
        // Initialize the database
        locations = locationsRepository.saveAndFlush(locations);
        locationsSearchRepository.save(locations);

        // Search the locations
        restLocationsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + locations.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locations.getId().intValue())))
            .andExpect(jsonPath("$.[*].locationId").value(hasItem(DEFAULT_LOCATION_ID)))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].stateProvince").value(hasItem(DEFAULT_STATE_PROVINCE)));
    }
}

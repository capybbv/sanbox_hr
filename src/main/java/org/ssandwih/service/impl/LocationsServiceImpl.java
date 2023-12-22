package org.ssandwih.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssandwih.domain.Locations;
import org.ssandwih.repository.LocationsRepository;
import org.ssandwih.repository.search.LocationsSearchRepository;
import org.ssandwih.service.LocationsService;

/**
 * Service Implementation for managing {@link org.ssandwih.domain.Locations}.
 */
@Service
@Transactional
public class LocationsServiceImpl implements LocationsService {

    private final Logger log = LoggerFactory.getLogger(LocationsServiceImpl.class);

    private final LocationsRepository locationsRepository;

    private final LocationsSearchRepository locationsSearchRepository;

    public LocationsServiceImpl(LocationsRepository locationsRepository, LocationsSearchRepository locationsSearchRepository) {
        this.locationsRepository = locationsRepository;
        this.locationsSearchRepository = locationsSearchRepository;
    }

    @Override
    public Locations save(Locations locations) {
        log.debug("Request to save Locations : {}", locations);
        Locations result = locationsRepository.save(locations);
        locationsSearchRepository.index(result);
        return result;
    }

    @Override
    public Locations update(Locations locations) {
        log.debug("Request to update Locations : {}", locations);
        Locations result = locationsRepository.save(locations);
        locationsSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Locations> partialUpdate(Locations locations) {
        log.debug("Request to partially update Locations : {}", locations);

        return locationsRepository
            .findById(locations.getId())
            .map(existingLocations -> {
                if (locations.getLocationId() != null) {
                    existingLocations.setLocationId(locations.getLocationId());
                }
                if (locations.getStreetAddress() != null) {
                    existingLocations.setStreetAddress(locations.getStreetAddress());
                }
                if (locations.getPostalCode() != null) {
                    existingLocations.setPostalCode(locations.getPostalCode());
                }
                if (locations.getCity() != null) {
                    existingLocations.setCity(locations.getCity());
                }
                if (locations.getStateProvince() != null) {
                    existingLocations.setStateProvince(locations.getStateProvince());
                }

                return existingLocations;
            })
            .map(locationsRepository::save)
            .map(savedLocations -> {
                locationsSearchRepository.index(savedLocations);
                return savedLocations;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Locations> findAll(Pageable pageable) {
        log.debug("Request to get all Locations");
        return locationsRepository.findAll(pageable);
    }

    public Page<Locations> findAllWithEagerRelationships(Pageable pageable) {
        return locationsRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Locations> findOne(Long id) {
        log.debug("Request to get Locations : {}", id);
        return locationsRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Locations : {}", id);
        locationsRepository.deleteById(id);
        locationsSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Locations> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Locations for query {}", query);
        return locationsSearchRepository.search(query, pageable);
    }
}

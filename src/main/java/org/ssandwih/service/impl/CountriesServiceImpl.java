package org.ssandwih.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssandwih.domain.Countries;
import org.ssandwih.repository.CountriesRepository;
import org.ssandwih.repository.search.CountriesSearchRepository;
import org.ssandwih.service.CountriesService;

/**
 * Service Implementation for managing {@link org.ssandwih.domain.Countries}.
 */
@Service
@Transactional
public class CountriesServiceImpl implements CountriesService {

    private final Logger log = LoggerFactory.getLogger(CountriesServiceImpl.class);

    private final CountriesRepository countriesRepository;

    private final CountriesSearchRepository countriesSearchRepository;

    public CountriesServiceImpl(CountriesRepository countriesRepository, CountriesSearchRepository countriesSearchRepository) {
        this.countriesRepository = countriesRepository;
        this.countriesSearchRepository = countriesSearchRepository;
    }

    @Override
    public Countries save(Countries countries) {
        log.debug("Request to save Countries : {}", countries);
        Countries result = countriesRepository.save(countries);
        countriesSearchRepository.index(result);
        return result;
    }

    @Override
    public Countries update(Countries countries) {
        log.debug("Request to update Countries : {}", countries);
        Countries result = countriesRepository.save(countries);
        countriesSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Countries> partialUpdate(Countries countries) {
        log.debug("Request to partially update Countries : {}", countries);

        return countriesRepository
            .findById(countries.getId())
            .map(existingCountries -> {
                if (countries.getCountryId() != null) {
                    existingCountries.setCountryId(countries.getCountryId());
                }
                if (countries.getCountryName() != null) {
                    existingCountries.setCountryName(countries.getCountryName());
                }

                return existingCountries;
            })
            .map(countriesRepository::save)
            .map(savedCountries -> {
                countriesSearchRepository.index(savedCountries);
                return savedCountries;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Countries> findAll(Pageable pageable) {
        log.debug("Request to get all Countries");
        return countriesRepository.findAll(pageable);
    }

    public Page<Countries> findAllWithEagerRelationships(Pageable pageable) {
        return countriesRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Countries> findOne(Long id) {
        log.debug("Request to get Countries : {}", id);
        return countriesRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Countries : {}", id);
        countriesRepository.deleteById(id);
        countriesSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Countries> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Countries for query {}", query);
        return countriesSearchRepository.search(query, pageable);
    }
}

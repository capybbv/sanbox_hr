package org.ssandwih.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ssandwih.domain.CountriesTestSamples.*;
import static org.ssandwih.domain.LocationsTestSamples.*;
import static org.ssandwih.domain.RegionTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.ssandwih.web.rest.TestUtil;

class CountriesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Countries.class);
        Countries countries1 = getCountriesSample1();
        Countries countries2 = new Countries();
        assertThat(countries1).isNotEqualTo(countries2);

        countries2.setId(countries1.getId());
        assertThat(countries1).isEqualTo(countries2);

        countries2 = getCountriesSample2();
        assertThat(countries1).isNotEqualTo(countries2);
    }

    @Test
    void locationsTest() throws Exception {
        Countries countries = getCountriesRandomSampleGenerator();
        Locations locationsBack = getLocationsRandomSampleGenerator();

        countries.addLocations(locationsBack);
        assertThat(countries.getLocations()).containsOnly(locationsBack);
        assertThat(locationsBack.getLocCIdFk()).isEqualTo(countries);

        countries.removeLocations(locationsBack);
        assertThat(countries.getLocations()).doesNotContain(locationsBack);
        assertThat(locationsBack.getLocCIdFk()).isNull();

        countries.locations(new HashSet<>(Set.of(locationsBack)));
        assertThat(countries.getLocations()).containsOnly(locationsBack);
        assertThat(locationsBack.getLocCIdFk()).isEqualTo(countries);

        countries.setLocations(new HashSet<>());
        assertThat(countries.getLocations()).doesNotContain(locationsBack);
        assertThat(locationsBack.getLocCIdFk()).isNull();
    }

    @Test
    void countrRegFkTest() throws Exception {
        Countries countries = getCountriesRandomSampleGenerator();
        Region regionBack = getRegionRandomSampleGenerator();

        countries.setCountrRegFk(regionBack);
        assertThat(countries.getCountrRegFk()).isEqualTo(regionBack);

        countries.countrRegFk(null);
        assertThat(countries.getCountrRegFk()).isNull();
    }
}

package org.ssandwih.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ssandwih.domain.CountriesTestSamples.*;
import static org.ssandwih.domain.DepartmentsTestSamples.*;
import static org.ssandwih.domain.LocationsTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.ssandwih.web.rest.TestUtil;

class LocationsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Locations.class);
        Locations locations1 = getLocationsSample1();
        Locations locations2 = new Locations();
        assertThat(locations1).isNotEqualTo(locations2);

        locations2.setId(locations1.getId());
        assertThat(locations1).isEqualTo(locations2);

        locations2 = getLocationsSample2();
        assertThat(locations1).isNotEqualTo(locations2);
    }

    @Test
    void departmentsTest() throws Exception {
        Locations locations = getLocationsRandomSampleGenerator();
        Departments departmentsBack = getDepartmentsRandomSampleGenerator();

        locations.addDepartments(departmentsBack);
        assertThat(locations.getDepartments()).containsOnly(departmentsBack);
        assertThat(departmentsBack.getDeptLocFk()).isEqualTo(locations);

        locations.removeDepartments(departmentsBack);
        assertThat(locations.getDepartments()).doesNotContain(departmentsBack);
        assertThat(departmentsBack.getDeptLocFk()).isNull();

        locations.departments(new HashSet<>(Set.of(departmentsBack)));
        assertThat(locations.getDepartments()).containsOnly(departmentsBack);
        assertThat(departmentsBack.getDeptLocFk()).isEqualTo(locations);

        locations.setDepartments(new HashSet<>());
        assertThat(locations.getDepartments()).doesNotContain(departmentsBack);
        assertThat(departmentsBack.getDeptLocFk()).isNull();
    }

    @Test
    void locCIdFkTest() throws Exception {
        Locations locations = getLocationsRandomSampleGenerator();
        Countries countriesBack = getCountriesRandomSampleGenerator();

        locations.setLocCIdFk(countriesBack);
        assertThat(locations.getLocCIdFk()).isEqualTo(countriesBack);

        locations.locCIdFk(null);
        assertThat(locations.getLocCIdFk()).isNull();
    }
}

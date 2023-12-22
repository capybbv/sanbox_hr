package org.ssandwih.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ssandwih.domain.CountriesTestSamples.*;
import static org.ssandwih.domain.RegionTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.ssandwih.web.rest.TestUtil;

class RegionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Region.class);
        Region region1 = getRegionSample1();
        Region region2 = new Region();
        assertThat(region1).isNotEqualTo(region2);

        region2.setId(region1.getId());
        assertThat(region1).isEqualTo(region2);

        region2 = getRegionSample2();
        assertThat(region1).isNotEqualTo(region2);
    }

    @Test
    void countriesTest() throws Exception {
        Region region = getRegionRandomSampleGenerator();
        Countries countriesBack = getCountriesRandomSampleGenerator();

        region.addCountries(countriesBack);
        assertThat(region.getCountries()).containsOnly(countriesBack);
        assertThat(countriesBack.getCountrRegFk()).isEqualTo(region);

        region.removeCountries(countriesBack);
        assertThat(region.getCountries()).doesNotContain(countriesBack);
        assertThat(countriesBack.getCountrRegFk()).isNull();

        region.countries(new HashSet<>(Set.of(countriesBack)));
        assertThat(region.getCountries()).containsOnly(countriesBack);
        assertThat(countriesBack.getCountrRegFk()).isEqualTo(region);

        region.setCountries(new HashSet<>());
        assertThat(region.getCountries()).doesNotContain(countriesBack);
        assertThat(countriesBack.getCountrRegFk()).isNull();
    }
}

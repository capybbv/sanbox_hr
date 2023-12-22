package org.ssandwih.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CountriesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Countries getCountriesSample1() {
        return new Countries().id(1L).countryId("countryId1").countryName("countryName1");
    }

    public static Countries getCountriesSample2() {
        return new Countries().id(2L).countryId("countryId2").countryName("countryName2");
    }

    public static Countries getCountriesRandomSampleGenerator() {
        return new Countries()
            .id(longCount.incrementAndGet())
            .countryId(UUID.randomUUID().toString())
            .countryName(UUID.randomUUID().toString());
    }
}

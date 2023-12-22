package org.ssandwih.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LocationsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Locations getLocationsSample1() {
        return new Locations()
            .id(1L)
            .locationId(1)
            .streetAddress("streetAddress1")
            .postalCode("postalCode1")
            .city("city1")
            .stateProvince("stateProvince1");
    }

    public static Locations getLocationsSample2() {
        return new Locations()
            .id(2L)
            .locationId(2)
            .streetAddress("streetAddress2")
            .postalCode("postalCode2")
            .city("city2")
            .stateProvince("stateProvince2");
    }

    public static Locations getLocationsRandomSampleGenerator() {
        return new Locations()
            .id(longCount.incrementAndGet())
            .locationId(intCount.incrementAndGet())
            .streetAddress(UUID.randomUUID().toString())
            .postalCode(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .stateProvince(UUID.randomUUID().toString());
    }
}

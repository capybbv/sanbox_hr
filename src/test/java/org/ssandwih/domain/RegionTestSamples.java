package org.ssandwih.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RegionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Region getRegionSample1() {
        return new Region().id(1L).regionId(1).regionName("regionName1");
    }

    public static Region getRegionSample2() {
        return new Region().id(2L).regionId(2).regionName("regionName2");
    }

    public static Region getRegionRandomSampleGenerator() {
        return new Region().id(longCount.incrementAndGet()).regionId(intCount.incrementAndGet()).regionName(UUID.randomUUID().toString());
    }
}

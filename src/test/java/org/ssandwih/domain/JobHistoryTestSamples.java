package org.ssandwih.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class JobHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static JobHistory getJobHistorySample1() {
        return new JobHistory().id(1L).employId(1);
    }

    public static JobHistory getJobHistorySample2() {
        return new JobHistory().id(2L).employId(2);
    }

    public static JobHistory getJobHistoryRandomSampleGenerator() {
        return new JobHistory().id(longCount.incrementAndGet()).employId(intCount.incrementAndGet());
    }
}

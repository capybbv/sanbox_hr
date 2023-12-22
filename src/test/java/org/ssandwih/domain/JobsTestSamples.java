package org.ssandwih.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class JobsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Jobs getJobsSample1() {
        return new Jobs().id(1L).jobId(1).jobTitle("jobTitle1").minSalary(1).maxSalary(1);
    }

    public static Jobs getJobsSample2() {
        return new Jobs().id(2L).jobId(2).jobTitle("jobTitle2").minSalary(2).maxSalary(2);
    }

    public static Jobs getJobsRandomSampleGenerator() {
        return new Jobs()
            .id(longCount.incrementAndGet())
            .jobId(intCount.incrementAndGet())
            .jobTitle(UUID.randomUUID().toString())
            .minSalary(intCount.incrementAndGet())
            .maxSalary(intCount.incrementAndGet());
    }
}

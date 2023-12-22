package org.ssandwih.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DepartmentsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Departments getDepartmentsSample1() {
        return new Departments().id(1L).departmentId(1).departmentName("departmentName1");
    }

    public static Departments getDepartmentsSample2() {
        return new Departments().id(2L).departmentId(2).departmentName("departmentName2");
    }

    public static Departments getDepartmentsRandomSampleGenerator() {
        return new Departments()
            .id(longCount.incrementAndGet())
            .departmentId(intCount.incrementAndGet())
            .departmentName(UUID.randomUUID().toString());
    }
}

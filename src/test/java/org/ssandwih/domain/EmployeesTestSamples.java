package org.ssandwih.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EmployeesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Employees getEmployeesSample1() {
        return new Employees()
            .id(1L)
            .employeeId(1)
            .firstName("firstName1")
            .lastName("lastName1")
            .email("email1")
            .phoneNumber("phoneNumber1")
            .salary(1)
            .commissionPct(1);
    }

    public static Employees getEmployeesSample2() {
        return new Employees()
            .id(2L)
            .employeeId(2)
            .firstName("firstName2")
            .lastName("lastName2")
            .email("email2")
            .phoneNumber("phoneNumber2")
            .salary(2)
            .commissionPct(2);
    }

    public static Employees getEmployeesRandomSampleGenerator() {
        return new Employees()
            .id(longCount.incrementAndGet())
            .employeeId(intCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .salary(intCount.incrementAndGet())
            .commissionPct(intCount.incrementAndGet());
    }
}

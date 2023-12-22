package org.ssandwih.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ssandwih.domain.EmployeesTestSamples.*;
import static org.ssandwih.domain.JobHistoryTestSamples.*;
import static org.ssandwih.domain.JobsTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.ssandwih.web.rest.TestUtil;

class JobsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Jobs.class);
        Jobs jobs1 = getJobsSample1();
        Jobs jobs2 = new Jobs();
        assertThat(jobs1).isNotEqualTo(jobs2);

        jobs2.setId(jobs1.getId());
        assertThat(jobs1).isEqualTo(jobs2);

        jobs2 = getJobsSample2();
        assertThat(jobs1).isNotEqualTo(jobs2);
    }

    @Test
    void employeesTest() throws Exception {
        Jobs jobs = getJobsRandomSampleGenerator();
        Employees employeesBack = getEmployeesRandomSampleGenerator();

        jobs.addEmployees(employeesBack);
        assertThat(jobs.getEmployees()).containsOnly(employeesBack);
        assertThat(employeesBack.getEmpJobFk()).isEqualTo(jobs);

        jobs.removeEmployees(employeesBack);
        assertThat(jobs.getEmployees()).doesNotContain(employeesBack);
        assertThat(employeesBack.getEmpJobFk()).isNull();

        jobs.employees(new HashSet<>(Set.of(employeesBack)));
        assertThat(jobs.getEmployees()).containsOnly(employeesBack);
        assertThat(employeesBack.getEmpJobFk()).isEqualTo(jobs);

        jobs.setEmployees(new HashSet<>());
        assertThat(jobs.getEmployees()).doesNotContain(employeesBack);
        assertThat(employeesBack.getEmpJobFk()).isNull();
    }

    @Test
    void jobHistoryTest() throws Exception {
        Jobs jobs = getJobsRandomSampleGenerator();
        JobHistory jobHistoryBack = getJobHistoryRandomSampleGenerator();

        jobs.addJobHistory(jobHistoryBack);
        assertThat(jobs.getJobHistories()).containsOnly(jobHistoryBack);
        assertThat(jobHistoryBack.getJhistJob()).isEqualTo(jobs);

        jobs.removeJobHistory(jobHistoryBack);
        assertThat(jobs.getJobHistories()).doesNotContain(jobHistoryBack);
        assertThat(jobHistoryBack.getJhistJob()).isNull();

        jobs.jobHistories(new HashSet<>(Set.of(jobHistoryBack)));
        assertThat(jobs.getJobHistories()).containsOnly(jobHistoryBack);
        assertThat(jobHistoryBack.getJhistJob()).isEqualTo(jobs);

        jobs.setJobHistories(new HashSet<>());
        assertThat(jobs.getJobHistories()).doesNotContain(jobHistoryBack);
        assertThat(jobHistoryBack.getJhistJob()).isNull();
    }
}

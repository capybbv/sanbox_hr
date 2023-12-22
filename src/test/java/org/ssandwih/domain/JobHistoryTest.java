package org.ssandwih.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ssandwih.domain.DepartmentsTestSamples.*;
import static org.ssandwih.domain.JobHistoryTestSamples.*;
import static org.ssandwih.domain.JobsTestSamples.*;

import org.junit.jupiter.api.Test;
import org.ssandwih.web.rest.TestUtil;

class JobHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JobHistory.class);
        JobHistory jobHistory1 = getJobHistorySample1();
        JobHistory jobHistory2 = new JobHistory();
        assertThat(jobHistory1).isNotEqualTo(jobHistory2);

        jobHistory2.setId(jobHistory1.getId());
        assertThat(jobHistory1).isEqualTo(jobHistory2);

        jobHistory2 = getJobHistorySample2();
        assertThat(jobHistory1).isNotEqualTo(jobHistory2);
    }

    @Test
    void jhistDepFkTest() throws Exception {
        JobHistory jobHistory = getJobHistoryRandomSampleGenerator();
        Departments departmentsBack = getDepartmentsRandomSampleGenerator();

        jobHistory.setJhistDepFk(departmentsBack);
        assertThat(jobHistory.getJhistDepFk()).isEqualTo(departmentsBack);

        jobHistory.jhistDepFk(null);
        assertThat(jobHistory.getJhistDepFk()).isNull();
    }

    @Test
    void jhistJobTest() throws Exception {
        JobHistory jobHistory = getJobHistoryRandomSampleGenerator();
        Jobs jobsBack = getJobsRandomSampleGenerator();

        jobHistory.setJhistJob(jobsBack);
        assertThat(jobHistory.getJhistJob()).isEqualTo(jobsBack);

        jobHistory.jhistJob(null);
        assertThat(jobHistory.getJhistJob()).isNull();
    }
}

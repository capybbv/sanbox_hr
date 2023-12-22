package org.ssandwih.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ssandwih.domain.DepartmentsTestSamples.*;
import static org.ssandwih.domain.EmployeesTestSamples.*;
import static org.ssandwih.domain.JobHistoryTestSamples.*;
import static org.ssandwih.domain.LocationsTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.ssandwih.web.rest.TestUtil;

class DepartmentsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Departments.class);
        Departments departments1 = getDepartmentsSample1();
        Departments departments2 = new Departments();
        assertThat(departments1).isNotEqualTo(departments2);

        departments2.setId(departments1.getId());
        assertThat(departments1).isEqualTo(departments2);

        departments2 = getDepartmentsSample2();
        assertThat(departments1).isNotEqualTo(departments2);
    }

    @Test
    void employeesTest() throws Exception {
        Departments departments = getDepartmentsRandomSampleGenerator();
        Employees employeesBack = getEmployeesRandomSampleGenerator();

        departments.addEmployees(employeesBack);
        assertThat(departments.getEmployees()).containsOnly(employeesBack);
        assertThat(employeesBack.getEmpDeptFk()).isEqualTo(departments);

        departments.removeEmployees(employeesBack);
        assertThat(departments.getEmployees()).doesNotContain(employeesBack);
        assertThat(employeesBack.getEmpDeptFk()).isNull();

        departments.employees(new HashSet<>(Set.of(employeesBack)));
        assertThat(departments.getEmployees()).containsOnly(employeesBack);
        assertThat(employeesBack.getEmpDeptFk()).isEqualTo(departments);

        departments.setEmployees(new HashSet<>());
        assertThat(departments.getEmployees()).doesNotContain(employeesBack);
        assertThat(employeesBack.getEmpDeptFk()).isNull();
    }

    @Test
    void jobHistoryTest() throws Exception {
        Departments departments = getDepartmentsRandomSampleGenerator();
        JobHistory jobHistoryBack = getJobHistoryRandomSampleGenerator();

        departments.addJobHistory(jobHistoryBack);
        assertThat(departments.getJobHistories()).containsOnly(jobHistoryBack);
        assertThat(jobHistoryBack.getJhistDepFk()).isEqualTo(departments);

        departments.removeJobHistory(jobHistoryBack);
        assertThat(departments.getJobHistories()).doesNotContain(jobHistoryBack);
        assertThat(jobHistoryBack.getJhistDepFk()).isNull();

        departments.jobHistories(new HashSet<>(Set.of(jobHistoryBack)));
        assertThat(departments.getJobHistories()).containsOnly(jobHistoryBack);
        assertThat(jobHistoryBack.getJhistDepFk()).isEqualTo(departments);

        departments.setJobHistories(new HashSet<>());
        assertThat(departments.getJobHistories()).doesNotContain(jobHistoryBack);
        assertThat(jobHistoryBack.getJhistDepFk()).isNull();
    }

    @Test
    void deptLocFkTest() throws Exception {
        Departments departments = getDepartmentsRandomSampleGenerator();
        Locations locationsBack = getLocationsRandomSampleGenerator();

        departments.setDeptLocFk(locationsBack);
        assertThat(departments.getDeptLocFk()).isEqualTo(locationsBack);

        departments.deptLocFk(null);
        assertThat(departments.getDeptLocFk()).isNull();
    }

    @Test
    void depMgrFkTest() throws Exception {
        Departments departments = getDepartmentsRandomSampleGenerator();
        Employees employeesBack = getEmployeesRandomSampleGenerator();

        departments.setDepMgrFk(employeesBack);
        assertThat(departments.getDepMgrFk()).isEqualTo(employeesBack);

        departments.depMgrFk(null);
        assertThat(departments.getDepMgrFk()).isNull();
    }
}

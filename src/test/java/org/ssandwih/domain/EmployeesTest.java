package org.ssandwih.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ssandwih.domain.DepartmentsTestSamples.*;
import static org.ssandwih.domain.EmployeesTestSamples.*;
import static org.ssandwih.domain.EmployeesTestSamples.*;
import static org.ssandwih.domain.JobsTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.ssandwih.web.rest.TestUtil;

class EmployeesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employees.class);
        Employees employees1 = getEmployeesSample1();
        Employees employees2 = new Employees();
        assertThat(employees1).isNotEqualTo(employees2);

        employees2.setId(employees1.getId());
        assertThat(employees1).isEqualTo(employees2);

        employees2 = getEmployeesSample2();
        assertThat(employees1).isNotEqualTo(employees2);
    }

    @Test
    void employeesTest() throws Exception {
        Employees employees = getEmployeesRandomSampleGenerator();
        Employees employeesBack = getEmployeesRandomSampleGenerator();

        employees.addEmployees(employeesBack);
        assertThat(employees.getEmployees()).containsOnly(employeesBack);
        assertThat(employeesBack.getEmpManagerFk()).isEqualTo(employees);

        employees.removeEmployees(employeesBack);
        assertThat(employees.getEmployees()).doesNotContain(employeesBack);
        assertThat(employeesBack.getEmpManagerFk()).isNull();

        employees.employees(new HashSet<>(Set.of(employeesBack)));
        assertThat(employees.getEmployees()).containsOnly(employeesBack);
        assertThat(employeesBack.getEmpManagerFk()).isEqualTo(employees);

        employees.setEmployees(new HashSet<>());
        assertThat(employees.getEmployees()).doesNotContain(employeesBack);
        assertThat(employeesBack.getEmpManagerFk()).isNull();
    }

    @Test
    void departmentsTest() throws Exception {
        Employees employees = getEmployeesRandomSampleGenerator();
        Departments departmentsBack = getDepartmentsRandomSampleGenerator();

        employees.addDepartments(departmentsBack);
        assertThat(employees.getDepartments()).containsOnly(departmentsBack);
        assertThat(departmentsBack.getDepMgrFk()).isEqualTo(employees);

        employees.removeDepartments(departmentsBack);
        assertThat(employees.getDepartments()).doesNotContain(departmentsBack);
        assertThat(departmentsBack.getDepMgrFk()).isNull();

        employees.departments(new HashSet<>(Set.of(departmentsBack)));
        assertThat(employees.getDepartments()).containsOnly(departmentsBack);
        assertThat(departmentsBack.getDepMgrFk()).isEqualTo(employees);

        employees.setDepartments(new HashSet<>());
        assertThat(employees.getDepartments()).doesNotContain(departmentsBack);
        assertThat(departmentsBack.getDepMgrFk()).isNull();
    }

    @Test
    void empManagerFkTest() throws Exception {
        Employees employees = getEmployeesRandomSampleGenerator();
        Employees employeesBack = getEmployeesRandomSampleGenerator();

        employees.setEmpManagerFk(employeesBack);
        assertThat(employees.getEmpManagerFk()).isEqualTo(employeesBack);

        employees.empManagerFk(null);
        assertThat(employees.getEmpManagerFk()).isNull();
    }

    @Test
    void empDeptFkTest() throws Exception {
        Employees employees = getEmployeesRandomSampleGenerator();
        Departments departmentsBack = getDepartmentsRandomSampleGenerator();

        employees.setEmpDeptFk(departmentsBack);
        assertThat(employees.getEmpDeptFk()).isEqualTo(departmentsBack);

        employees.empDeptFk(null);
        assertThat(employees.getEmpDeptFk()).isNull();
    }

    @Test
    void empJobFkTest() throws Exception {
        Employees employees = getEmployeesRandomSampleGenerator();
        Jobs jobsBack = getJobsRandomSampleGenerator();

        employees.setEmpJobFk(jobsBack);
        assertThat(employees.getEmpJobFk()).isEqualTo(jobsBack);

        employees.empJobFk(null);
        assertThat(employees.getEmpJobFk()).isNull();
    }
}

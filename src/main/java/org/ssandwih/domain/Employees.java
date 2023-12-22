package org.ssandwih.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Employees.
 */
@Entity
@Table(name = "employees")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "employees")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Employees implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "employee_id", nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer employeeId;

    @Size(max = 20)
    @Column(name = "first_name", length = 20)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String firstName;

    @NotNull
    @Size(max = 25)
    @Column(name = "last_name", length = 25, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String lastName;

    @NotNull
    @Size(max = 25)
    @Column(name = "email", length = 25, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String email;

    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phoneNumber;

    @NotNull
    @Column(name = "hire_date", nullable = false)
    private ZonedDateTime hireDate;

    @Column(name = "salary")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer salary;

    @Column(name = "commission_pct")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer commissionPct;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "empManagerFk")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "employees", "departments", "empManagerFk", "empDeptFk", "empJobFk" }, allowSetters = true)
    private Set<Employees> employees = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "depMgrFk")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "employees", "jobHistories", "deptLocFk", "depMgrFk" }, allowSetters = true)
    private Set<Departments> departments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "employees", "departments", "empManagerFk", "empDeptFk", "empJobFk" }, allowSetters = true)
    private Employees empManagerFk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "employees", "jobHistories", "deptLocFk", "depMgrFk" }, allowSetters = true)
    private Departments empDeptFk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "employees", "jobHistories" }, allowSetters = true)
    private Jobs empJobFk;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Employees id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEmployeeId() {
        return this.employeeId;
    }

    public Employees employeeId(Integer employeeId) {
        this.setEmployeeId(employeeId);
        return this;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Employees firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Employees lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public Employees email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Employees phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ZonedDateTime getHireDate() {
        return this.hireDate;
    }

    public Employees hireDate(ZonedDateTime hireDate) {
        this.setHireDate(hireDate);
        return this;
    }

    public void setHireDate(ZonedDateTime hireDate) {
        this.hireDate = hireDate;
    }

    public Integer getSalary() {
        return this.salary;
    }

    public Employees salary(Integer salary) {
        this.setSalary(salary);
        return this;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Integer getCommissionPct() {
        return this.commissionPct;
    }

    public Employees commissionPct(Integer commissionPct) {
        this.setCommissionPct(commissionPct);
        return this;
    }

    public void setCommissionPct(Integer commissionPct) {
        this.commissionPct = commissionPct;
    }

    public Set<Employees> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employees> employees) {
        if (this.employees != null) {
            this.employees.forEach(i -> i.setEmpManagerFk(null));
        }
        if (employees != null) {
            employees.forEach(i -> i.setEmpManagerFk(this));
        }
        this.employees = employees;
    }

    public Employees employees(Set<Employees> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Employees addEmployees(Employees employees) {
        this.employees.add(employees);
        employees.setEmpManagerFk(this);
        return this;
    }

    public Employees removeEmployees(Employees employees) {
        this.employees.remove(employees);
        employees.setEmpManagerFk(null);
        return this;
    }

    public Set<Departments> getDepartments() {
        return this.departments;
    }

    public void setDepartments(Set<Departments> departments) {
        if (this.departments != null) {
            this.departments.forEach(i -> i.setDepMgrFk(null));
        }
        if (departments != null) {
            departments.forEach(i -> i.setDepMgrFk(this));
        }
        this.departments = departments;
    }

    public Employees departments(Set<Departments> departments) {
        this.setDepartments(departments);
        return this;
    }

    public Employees addDepartments(Departments departments) {
        this.departments.add(departments);
        departments.setDepMgrFk(this);
        return this;
    }

    public Employees removeDepartments(Departments departments) {
        this.departments.remove(departments);
        departments.setDepMgrFk(null);
        return this;
    }

    public Employees getEmpManagerFk() {
        return this.empManagerFk;
    }

    public void setEmpManagerFk(Employees employees) {
        this.empManagerFk = employees;
    }

    public Employees empManagerFk(Employees employees) {
        this.setEmpManagerFk(employees);
        return this;
    }

    public Departments getEmpDeptFk() {
        return this.empDeptFk;
    }

    public void setEmpDeptFk(Departments departments) {
        this.empDeptFk = departments;
    }

    public Employees empDeptFk(Departments departments) {
        this.setEmpDeptFk(departments);
        return this;
    }

    public Jobs getEmpJobFk() {
        return this.empJobFk;
    }

    public void setEmpJobFk(Jobs jobs) {
        this.empJobFk = jobs;
    }

    public Employees empJobFk(Jobs jobs) {
        this.setEmpJobFk(jobs);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employees)) {
            return false;
        }
        return getId() != null && getId().equals(((Employees) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Employees{" +
            "id=" + getId() +
            ", employeeId=" + getEmployeeId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", hireDate='" + getHireDate() + "'" +
            ", salary=" + getSalary() +
            ", commissionPct=" + getCommissionPct() +
            "}";
    }
}

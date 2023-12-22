package org.ssandwih.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Departments.
 */
@Entity
@Table(name = "departments")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "departments")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Departments implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "department_id", nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer departmentId;

    @Size(max = 30)
    @Column(name = "department_name", length = 30)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String departmentName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "empDeptFk")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "employees", "departments", "empManagerFk", "empDeptFk", "empJobFk" }, allowSetters = true)
    private Set<Employees> employees = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jhistDepFk")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "jhistDepFk", "jhistJob" }, allowSetters = true)
    private Set<JobHistory> jobHistories = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "departments", "locCIdFk" }, allowSetters = true)
    private Locations deptLocFk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "employees", "departments", "empManagerFk", "empDeptFk", "empJobFk" }, allowSetters = true)
    private Employees depMgrFk;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Departments id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDepartmentId() {
        return this.departmentId;
    }

    public Departments departmentId(Integer departmentId) {
        this.setDepartmentId(departmentId);
        return this;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public Departments departmentName(String departmentName) {
        this.setDepartmentName(departmentName);
        return this;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Set<Employees> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employees> employees) {
        if (this.employees != null) {
            this.employees.forEach(i -> i.setEmpDeptFk(null));
        }
        if (employees != null) {
            employees.forEach(i -> i.setEmpDeptFk(this));
        }
        this.employees = employees;
    }

    public Departments employees(Set<Employees> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Departments addEmployees(Employees employees) {
        this.employees.add(employees);
        employees.setEmpDeptFk(this);
        return this;
    }

    public Departments removeEmployees(Employees employees) {
        this.employees.remove(employees);
        employees.setEmpDeptFk(null);
        return this;
    }

    public Set<JobHistory> getJobHistories() {
        return this.jobHistories;
    }

    public void setJobHistories(Set<JobHistory> jobHistories) {
        if (this.jobHistories != null) {
            this.jobHistories.forEach(i -> i.setJhistDepFk(null));
        }
        if (jobHistories != null) {
            jobHistories.forEach(i -> i.setJhistDepFk(this));
        }
        this.jobHistories = jobHistories;
    }

    public Departments jobHistories(Set<JobHistory> jobHistories) {
        this.setJobHistories(jobHistories);
        return this;
    }

    public Departments addJobHistory(JobHistory jobHistory) {
        this.jobHistories.add(jobHistory);
        jobHistory.setJhistDepFk(this);
        return this;
    }

    public Departments removeJobHistory(JobHistory jobHistory) {
        this.jobHistories.remove(jobHistory);
        jobHistory.setJhistDepFk(null);
        return this;
    }

    public Locations getDeptLocFk() {
        return this.deptLocFk;
    }

    public void setDeptLocFk(Locations locations) {
        this.deptLocFk = locations;
    }

    public Departments deptLocFk(Locations locations) {
        this.setDeptLocFk(locations);
        return this;
    }

    public Employees getDepMgrFk() {
        return this.depMgrFk;
    }

    public void setDepMgrFk(Employees employees) {
        this.depMgrFk = employees;
    }

    public Departments depMgrFk(Employees employees) {
        this.setDepMgrFk(employees);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Departments)) {
            return false;
        }
        return getId() != null && getId().equals(((Departments) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Departments{" +
            "id=" + getId() +
            ", departmentId=" + getDepartmentId() +
            ", departmentName='" + getDepartmentName() + "'" +
            "}";
    }
}

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
 * A Jobs.
 */
@Entity
@Table(name = "jobs")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "jobs")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Jobs implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "job_id", nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer jobId;

    @NotNull
    @Size(max = 35)
    @Column(name = "job_title", length = 35, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String jobTitle;

    @Column(name = "min_salary")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer minSalary;

    @Column(name = "max_salary")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer maxSalary;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "empJobFk")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "employees", "departments", "empManagerFk", "empDeptFk", "empJobFk" }, allowSetters = true)
    private Set<Employees> employees = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jhistJob")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "jhistDepFk", "jhistJob" }, allowSetters = true)
    private Set<JobHistory> jobHistories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Jobs id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getJobId() {
        return this.jobId;
    }

    public Jobs jobId(Integer jobId) {
        this.setJobId(jobId);
        return this;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return this.jobTitle;
    }

    public Jobs jobTitle(String jobTitle) {
        this.setJobTitle(jobTitle);
        return this;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Integer getMinSalary() {
        return this.minSalary;
    }

    public Jobs minSalary(Integer minSalary) {
        this.setMinSalary(minSalary);
        return this;
    }

    public void setMinSalary(Integer minSalary) {
        this.minSalary = minSalary;
    }

    public Integer getMaxSalary() {
        return this.maxSalary;
    }

    public Jobs maxSalary(Integer maxSalary) {
        this.setMaxSalary(maxSalary);
        return this;
    }

    public void setMaxSalary(Integer maxSalary) {
        this.maxSalary = maxSalary;
    }

    public Set<Employees> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employees> employees) {
        if (this.employees != null) {
            this.employees.forEach(i -> i.setEmpJobFk(null));
        }
        if (employees != null) {
            employees.forEach(i -> i.setEmpJobFk(this));
        }
        this.employees = employees;
    }

    public Jobs employees(Set<Employees> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Jobs addEmployees(Employees employees) {
        this.employees.add(employees);
        employees.setEmpJobFk(this);
        return this;
    }

    public Jobs removeEmployees(Employees employees) {
        this.employees.remove(employees);
        employees.setEmpJobFk(null);
        return this;
    }

    public Set<JobHistory> getJobHistories() {
        return this.jobHistories;
    }

    public void setJobHistories(Set<JobHistory> jobHistories) {
        if (this.jobHistories != null) {
            this.jobHistories.forEach(i -> i.setJhistJob(null));
        }
        if (jobHistories != null) {
            jobHistories.forEach(i -> i.setJhistJob(this));
        }
        this.jobHistories = jobHistories;
    }

    public Jobs jobHistories(Set<JobHistory> jobHistories) {
        this.setJobHistories(jobHistories);
        return this;
    }

    public Jobs addJobHistory(JobHistory jobHistory) {
        this.jobHistories.add(jobHistory);
        jobHistory.setJhistJob(this);
        return this;
    }

    public Jobs removeJobHistory(JobHistory jobHistory) {
        this.jobHistories.remove(jobHistory);
        jobHistory.setJhistJob(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Jobs)) {
            return false;
        }
        return getId() != null && getId().equals(((Jobs) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Jobs{" +
            "id=" + getId() +
            ", jobId=" + getJobId() +
            ", jobTitle='" + getJobTitle() + "'" +
            ", minSalary=" + getMinSalary() +
            ", maxSalary=" + getMaxSalary() +
            "}";
    }
}

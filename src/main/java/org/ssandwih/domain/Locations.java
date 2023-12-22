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
 * A Locations.
 */
@Entity
@Table(name = "locations")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "locations")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Locations implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "location_id", nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer locationId;

    @Size(max = 40)
    @Column(name = "street_address", length = 40)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String streetAddress;

    @Size(max = 12)
    @Column(name = "postal_code", length = 12)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String postalCode;

    @Size(max = 30)
    @Column(name = "city", length = 30)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String city;

    @Size(max = 25)
    @Column(name = "state_province", length = 25)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String stateProvince;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "deptLocFk")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "employees", "jobHistories", "deptLocFk", "depMgrFk" }, allowSetters = true)
    private Set<Departments> departments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "locations", "countrRegFk" }, allowSetters = true)
    private Countries locCIdFk;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Locations id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLocationId() {
        return this.locationId;
    }

    public Locations locationId(Integer locationId) {
        this.setLocationId(locationId);
        return this;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getStreetAddress() {
        return this.streetAddress;
    }

    public Locations streetAddress(String streetAddress) {
        this.setStreetAddress(streetAddress);
        return this;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public Locations postalCode(String postalCode) {
        this.setPostalCode(postalCode);
        return this;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return this.city;
    }

    public Locations city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return this.stateProvince;
    }

    public Locations stateProvince(String stateProvince) {
        this.setStateProvince(stateProvince);
        return this;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public Set<Departments> getDepartments() {
        return this.departments;
    }

    public void setDepartments(Set<Departments> departments) {
        if (this.departments != null) {
            this.departments.forEach(i -> i.setDeptLocFk(null));
        }
        if (departments != null) {
            departments.forEach(i -> i.setDeptLocFk(this));
        }
        this.departments = departments;
    }

    public Locations departments(Set<Departments> departments) {
        this.setDepartments(departments);
        return this;
    }

    public Locations addDepartments(Departments departments) {
        this.departments.add(departments);
        departments.setDeptLocFk(this);
        return this;
    }

    public Locations removeDepartments(Departments departments) {
        this.departments.remove(departments);
        departments.setDeptLocFk(null);
        return this;
    }

    public Countries getLocCIdFk() {
        return this.locCIdFk;
    }

    public void setLocCIdFk(Countries countries) {
        this.locCIdFk = countries;
    }

    public Locations locCIdFk(Countries countries) {
        this.setLocCIdFk(countries);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Locations)) {
            return false;
        }
        return getId() != null && getId().equals(((Locations) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Locations{" +
            "id=" + getId() +
            ", locationId=" + getLocationId() +
            ", streetAddress='" + getStreetAddress() + "'" +
            ", postalCode='" + getPostalCode() + "'" +
            ", city='" + getCity() + "'" +
            ", stateProvince='" + getStateProvince() + "'" +
            "}";
    }
}

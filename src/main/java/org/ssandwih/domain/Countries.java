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
 * A Countries.
 */
@Entity
@Table(name = "countries")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "countries")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Countries implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 2)
    @Column(name = "country_id", length = 2, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String countryId;

    @Column(name = "country_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String countryName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "locCIdFk")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "departments", "locCIdFk" }, allowSetters = true)
    private Set<Locations> locations = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "countries" }, allowSetters = true)
    private Region countrRegFk;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Countries id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryId() {
        return this.countryId;
    }

    public Countries countryId(String countryId) {
        this.setCountryId(countryId);
        return this;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public Countries countryName(String countryName) {
        this.setCountryName(countryName);
        return this;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Set<Locations> getLocations() {
        return this.locations;
    }

    public void setLocations(Set<Locations> locations) {
        if (this.locations != null) {
            this.locations.forEach(i -> i.setLocCIdFk(null));
        }
        if (locations != null) {
            locations.forEach(i -> i.setLocCIdFk(this));
        }
        this.locations = locations;
    }

    public Countries locations(Set<Locations> locations) {
        this.setLocations(locations);
        return this;
    }

    public Countries addLocations(Locations locations) {
        this.locations.add(locations);
        locations.setLocCIdFk(this);
        return this;
    }

    public Countries removeLocations(Locations locations) {
        this.locations.remove(locations);
        locations.setLocCIdFk(null);
        return this;
    }

    public Region getCountrRegFk() {
        return this.countrRegFk;
    }

    public void setCountrRegFk(Region region) {
        this.countrRegFk = region;
    }

    public Countries countrRegFk(Region region) {
        this.setCountrRegFk(region);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Countries)) {
            return false;
        }
        return getId() != null && getId().equals(((Countries) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Countries{" +
            "id=" + getId() +
            ", countryId='" + getCountryId() + "'" +
            ", countryName='" + getCountryName() + "'" +
            "}";
    }
}

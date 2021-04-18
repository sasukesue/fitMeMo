package com.nus.fitmegw.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Location.
 */
@Table("location")
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("branch_name")
    private String branchName;

    @NotNull(message = "must not be null")
    @Column("address")
    private String address;

    @Transient
    @JsonIgnoreProperties(value = { "schLoc", "schClass" }, allowSetters = true)
    private Set<Schedule> locSches = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location id(Long id) {
        this.id = id;
        return this;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public Location branchName(String branchName) {
        this.branchName = branchName;
        return this;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAddress() {
        return this.address;
    }

    public Location address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Schedule> getLocSches() {
        return this.locSches;
    }

    public Location locSches(Set<Schedule> schedules) {
        this.setLocSches(schedules);
        return this;
    }

    public Location addLocSch(Schedule schedule) {
        this.locSches.add(schedule);
        schedule.setSchLoc(this);
        return this;
    }

    public Location removeLocSch(Schedule schedule) {
        this.locSches.remove(schedule);
        schedule.setSchLoc(null);
        return this;
    }

    public void setLocSches(Set<Schedule> schedules) {
        if (this.locSches != null) {
            this.locSches.forEach(i -> i.setSchLoc(null));
        }
        if (schedules != null) {
            schedules.forEach(i -> i.setSchLoc(this));
        }
        this.locSches = schedules;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Location)) {
            return false;
        }
        return id != null && id.equals(((Location) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Location{" +
            "id=" + getId() +
            ", branchName='" + getBranchName() + "'" +
            ", address='" + getAddress() + "'" +
            "}";
    }
}

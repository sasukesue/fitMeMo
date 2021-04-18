package com.nus.fitmegw.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.nus.fitmegw.domain.FitnessClass} entity.
 */
public class FitnessClassDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String className;

    @NotNull(message = "must not be null")
    private Integer duration;

    @NotNull(message = "must not be null")
    private String level;

    @NotNull(message = "must not be null")
    private String instructorName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FitnessClassDTO)) {
            return false;
        }

        FitnessClassDTO fitnessClassDTO = (FitnessClassDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fitnessClassDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FitnessClassDTO{" +
            "id=" + getId() +
            ", className='" + getClassName() + "'" +
            ", duration=" + getDuration() +
            ", level='" + getLevel() + "'" +
            ", instructorName='" + getInstructorName() + "'" +
            "}";
    }
}

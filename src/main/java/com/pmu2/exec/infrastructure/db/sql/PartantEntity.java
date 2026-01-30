package com.pmu2.exec.infrastructure.db.sql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Objects;

/**
 * Represents a Partant in the database.
 * This entity is mapped to the "partant" table in the database.
 *
 * @author HelderPM
 * @since 1.0
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "partant")
public class PartantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false)
    @NotBlank(message = "Your Partant needs a name.")
    private String name;

    @Column(name = "numero", nullable = false)
    private int number;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CourseEntity> courses;

    // Manual getters to ensure compilation works
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public List<CourseEntity> getCourses() {
        return courses;
    }

    // Manual setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setCourses(List<CourseEntity> courses) {
        this.courses = courses;
    }

    /**
     * Constructor for creating a new PartantEntity with the given name and number.
     *
     * @param name  The name of the Partant.
     * @param number  The number of the Partant.
     */
    public PartantEntity(String name, int number) {
        this.name = name;
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        PartantEntity that = (PartantEntity) o;
        
        // Use business key for equality if possible, otherwise use ID with null safety
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }
        
        // For transient entities, compare business fields (name + number is a natural key)
        return number == that.number &&
               Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        // Use business key for hash code if possible, otherwise use ID with null safety
        if (id != null) {
            return Objects.hash(id);
        }
        
        // For transient entities, hash business fields (name + number is a natural key)
        return Objects.hash(name, number);
    }

}


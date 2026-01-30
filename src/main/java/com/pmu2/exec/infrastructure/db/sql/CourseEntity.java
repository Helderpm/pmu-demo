package com.pmu2.exec.infrastructure.db.sql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Course in the database.
 * <p>
 * This class is an entity that maps to the "courses" table in the database.
 * It contains fields for the course's ID, name, number, date, and a list of participants.
 *
 * @author HelderPM
 * @since 1.0
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "courses")
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long courseId;

    @Column(name = "nom", nullable = false)
    @NotBlank(message = "Your Course needs a name.")
    private String name;

    @Column(name = "numero", nullable = false)
    private int number;

    @Column(name = "date")
    private LocalDate date;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<PartantEntity> partants;

    // Manual getters to ensure compilation works
    public Long getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<PartantEntity> getPartants() {
        return partants;
    }

    // Manual setters
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPartants(List<PartantEntity> partants) {
        this.partants = partants;
    }


    /**
     * Constructor for creating a new CourseEntity with the given name, number, and date.
     *
     * @param name The name of the course.
     * @param number The number of the course.
     * @param date The date of the course.
     */
    public CourseEntity(String name, int number, LocalDate date) {
        this.name = name;
        this.number = number;
        this.date = date;
    }

    
    /**
     * Constructs a new instance of {@link CourseEntity} with the given name, number, date, and list of participants.
     *
     * @param name The name of the course. It cannot be null or empty.
     * @param number The number of the course.
     * @param date The date of the course.
     * @param partants The list of participants in the course.
     */
    public CourseEntity(String name, int number, LocalDate date, List<PartantEntity> partants) {
        this.name = name;
        this.number = number;
        this.date = date;
        this.partants = partants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CourseEntity that = (CourseEntity) o;
        
        // Use business key for equality if possible, otherwise use ID with null safety
        if (courseId != null && that.courseId != null) {
            return Objects.equals(courseId, that.courseId);
        }
        
        // For transient entities, compare business fields (name + number + date is a natural key)
        return number == that.number &&
               Objects.equals(name, that.name) &&
               Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        // Use business key for hash code if possible, otherwise use ID with null safety
        if (courseId != null) {
            return Objects.hash(courseId);
        }
        
        // For transient entities, hash business fields (name + number + date is a natural key)
        return Objects.hash(name, number, date);
    }

}


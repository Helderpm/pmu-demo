package com.pmu2.exec.infrastrure.db.sql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a Course in the database.
 *
 * This class is an entity that maps to the "courses" table in the database.
 * It contains fields for the course's ID, name, number, date, and a list of participants.
 *
 * @author HelderPM
 * @since 1.0
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "courseId")
@Table(name = "courses")
public class CourseEntity {

    /**
     * The unique identifier for the course.
     * This field is annotated with {@link Id} to mark it as the primary key, and {@link GeneratedValue} to indicate that the
     * value should be generated automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long courseId;

    /**
     * The name of the course.
     * This field is annotated with {@link Column} to specify the column name in the database, and {@link NotBlank} to
     * enforce a validation constraint that the name cannot be null or empty.
     */
    @Column(name = "nom", nullable = false)
    @NotBlank(message = "Your Course needs a name.")
    private String name;

    /**
     * The number of the course.
     * This field is annotated with {@link Column} to specify the column name in the database.
     */
    @Column(name = "numero", nullable = false)
    private int number;

    /**
     * The date of the course.
     * This field is annotated with {@link Column} to specify the column name in the database.
     */
    @Column(name = "date")
    private LocalDate date;

    /**
     * The list of participants in the course.
     * This field is annotated with {@link OneToMany} to establish a one-to-many relationship with the {@link PartantEntity}
     * class. The {@link CascadeType} is set to ALL to propagate changes to the related entities, the {@link FetchType} is
     * set to EAGER to fetch the related entities eagerly, and {@link orphanRemoval} is set to true to automatically remove
     * orphaned entities.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<PartantEntity> partants;


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

}

package com.pmu2.exec.infrastrure.db.sql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "courseId")
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


    public CourseEntity(String name, int number, LocalDate date) {
        this.name = name;
        this.number = number;
        this.date = date;
    }

}

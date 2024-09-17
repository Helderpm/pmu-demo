package com.pmu2.exec.infrastrure.db.sql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
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

    public PartantEntity(String name, int number) {
        this.name = name;
        this.number = number;
    }

}

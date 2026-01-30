package com.pmu2.exec.infrastructure.db.sql;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "inscription")
class InscriptionEntity {
    @EmbeddedId
    private InscriptionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("partantId")
    private PartantEntity partant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    private CourseEntity course;

    // Private inner class for InscriptionId
    @Embeddable
    private static class InscriptionId implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Long partantId;
        private Long courseId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InscriptionId that = (InscriptionId) o;
            return Objects.equals(partantId, 
                    that.partantId) &&
                    Objects.equals(courseId, that.courseId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(partantId, courseId);
        }
    }
}


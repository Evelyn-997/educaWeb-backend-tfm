package com.tfm.edcuaweb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="calificaciones")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Long id;
    @ElementCollection
    @CollectionTable(name = "notas_actividades", joinColumns = @JoinColumn(name = "activity_id"))
    private List<ActivityGrade> activities = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "notas_examenes", joinColumns = @JoinColumn(name = "exam_id"))
    private List<ExamGrades> exams = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}

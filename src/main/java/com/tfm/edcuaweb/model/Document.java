package com.tfm.edcuaweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name="documentos")
public class Document {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;
    private String name;
    private String filePath;
    private String version; //Control de versiones
    private LocalDate uploadDate;
    private String type;

    @ManyToOne //(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;
    @ManyToOne //(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

}

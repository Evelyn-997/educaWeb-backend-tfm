package com.tfm.edcuaweb.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name="cursos")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Course {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
	private Long id;
	@NotBlank
	@Column(nullable = false, length = 120)
	private String name;
	@NotBlank
	@Column(nullable = false, length = 50, unique = true)
	private String code;
	@Column(length = 500)
	private String description;
    /* PROSEFOR DEL CURSO*/
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher_id")
	private User teacher;
    @Column( length = 120)
    private String teacherName;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonIgnore // evita devolver lista completa de documentos cuando obtienes el curso
    private List<Document> documents;
	
	// Estudiantes matriculados
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "curso_estudiantes",
			joinColumns = @JoinColumn(name = "course_id"),
	      	inverseJoinColumns = @JoinColumn(name = "student_id")
	  )
	private Set<User> students = new HashSet<>();
	
	@Column(nullable = false, updatable = false)
	private Instant createdAt =Instant.now();

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Grade> grades;


    @PrePersist
	public void prePersist() {
		if(createdAt == null) {
			createdAt = Instant.now();
		}
	}

}
	

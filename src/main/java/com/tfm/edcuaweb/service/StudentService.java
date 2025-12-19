package com.tfm.edcuaweb.service;

import com.tfm.edcuaweb.dto.CourseResponse;
import com.tfm.edcuaweb.dto.StudentProgressResponse;
import com.tfm.edcuaweb.model.*;
import com.tfm.edcuaweb.repository.CourseRepository;
import com.tfm.edcuaweb.repository.GradeRepository;
import com.tfm.edcuaweb.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Builder
@RequiredArgsConstructor
public class StudentService {

    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final GradeRepository gradeRepo;

    /**
     * Obtener todos los cursos en los que un estudiante está matriculado.
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getMyCourses(String username) {
        //Buscar usuario
        User student = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
        //Verificar rol estudiante
        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Solo un estudiante puede acceder a sus cursos.");
        }
        // Encontrar cursos donde está matriculado
        List<Course> courses = courseRepo.findAllByStudentId(student.getId());
        //Mapear
        return courses.stream()
                .map(c -> map(c)) // sin estudiantes dentro
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseDetails(Long courseId, User student) {
        //Buscamos el curso
        Course c = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado o no te pertenece."));
        //Verifico que el studiante pertece al curso
        if (c.getStudents() == null || c.getStudents().stream().noneMatch(s -> s.getId().equals(student.getId()))) {
            throw new IllegalArgumentException("No estás matriculado en este curso.");
        }
        //mapeo Course
        return map(c);
    }

    public CourseResponse getMyCourseById(Long id, String username) {
        User student = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return getCourseDetails(id, student);
    }

    public StudentProgressResponse getStudentProgress(User student,Long courseId) {
        //Obtener todas las notas
        Grade grades = gradeRepo.findByCourseIdAndStudentId(courseId,student.getId())
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        List<ActivityGrade> activities = grades.getActivities();
        List<ExamGrades> exams = grades.getExams();
        // Calcular media global
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (ActivityGrade a : activities) {
            if (a.getGrade() != null) {
                total = total.add(a.getGrade());
                count++;
            }
        }


        for (ExamGrades e : exams) {
            if (e.getGrade() != null) {
                total = total.add(e.getGrade());
                count++;
            }
        }
        double average = count > 0
                ? total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        StudentProgressResponse progress = new StudentProgressResponse();
        progress.setGradeAverage(average);
        progress.setActivityGrades(activities);
        progress.setExamGrades(exams);
        return progress;
    }

    public Map<Long, StudentProgressResponse> getAllProgress(User student) {
        //  Obtener todas las calificaciones del alumno
        List<Grade> grades = gradeRepo.findAllByStudentId(student.getId());
        Map<Long, StudentProgressResponse> result = new HashMap<>();
        // Construir progreso por curso
        for (Grade grade : grades) {
            BigDecimal total = BigDecimal.ZERO;
            int count = 0;
            // Actividades
            for (ActivityGrade a : grade.getActivities()) {
                if (a.getGrade() != null) {
                    total = total.add(a.getGrade());
                    count++;
                }
            }
            // Exámenes
            for (ExamGrades e : grade.getExams()) {
                if (e.getGrade() != null) {
                    total = total.add(e.getGrade());
                    count++;
                }
            }
            double average = count > 0
                    ? total.divide(
                    BigDecimal.valueOf(count),
                    2,
                    RoundingMode.HALF_UP
            ).doubleValue()
                    : 0.0;
            StudentProgressResponse response = new StudentProgressResponse(
                    average,
                    grade.getActivities(),
                    grade.getExams()
            );
            // clave = ID del curso
            result.put(
                    grade.getCourse().getId(),
                    response
            );
        }
        return result;
    }


    /** Mapea Course → CourseResponse (sin estudiantes) */
    private CourseResponse map(Course course) {

        CourseResponse resp = new CourseResponse();
        resp.setId(course.getId());
        resp.setName(course.getName());
        resp.setDescription(course.getDescription());
        resp.setCode(course.getCode());
        resp.setCreatedAt(course.getCreatedAt());
        // Información del profesor
        if (course.getTeacher() != null) {
            resp.setTeacherId(course.getTeacher().getId());
            resp.setTeacherName(course.getTeacher().getName() + " " + course.getTeacher().getLastName());
            resp.setTeacherUsername(course.getTeacher().getUsername());
        }
        // Los estudiantes no se cargan
        resp.setStudents(null);
        resp.setStudentsCount(course.getStudents() != null ? course.getStudents().size() : 0);
        return resp;
    }
}

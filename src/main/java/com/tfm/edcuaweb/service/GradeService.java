package com.tfm.edcuaweb.service;

import com.tfm.edcuaweb.dto.GradeRequest;
import com.tfm.edcuaweb.dto.GradeResponse;
import com.tfm.edcuaweb.model.ActivityGrade;
import com.tfm.edcuaweb.model.ExamGrades;
import com.tfm.edcuaweb.model.Course;
import com.tfm.edcuaweb.model.Grade;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.repository.CourseRepository;
import com.tfm.edcuaweb.repository.GradeRepository;
import com.tfm.edcuaweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    // Guardas notas
    public void saveGrade(Long courseId, Long studentId, GradeRequest req) {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        Grade grade = gradeRepo
                .findByCourseIdAndStudentId(courseId, studentId)
                .orElse(new Grade());

        grade.setCourse(course);
        grade.setStudent(student);
        // VALIDACIÓN
        validateGradeList(req.getActivities());
        validateGradeList(req.getExams());

        grade.setActivities(req.getActivities());
        grade.setExams(req.getExams());

        gradeRepo.save(grade);
    }
    // Validar las notas
    private void validateGradeList(List<?> list) {
        if (list == null) return;
        for (Object item : list) {
            BigDecimal grade = null;

            if(item instanceof ActivityGrade act){
                grade = act.getGrade();
                System.out.println("Actividad → grade = " + grade);
            }else if(item instanceof ExamGrades exam){
                grade = exam.getGrade();
                System.out.println("Examen → grade = " + grade);
            }else{
                throw new IllegalArgumentException("Tipo de elemento no válido en la lista de calificaciones.");
            }
            //Validar rango de notas
            if( grade == null ||
                grade.compareTo(BigDecimal.ZERO) < 0 ||
                grade.compareTo(new BigDecimal("10")) > 0){
                    throw new IllegalArgumentException("Las notas deben estar entre 0 y 10.");
            }
        }
    }
    // NOTAS de un ESTUDIANTE
    public GradeResponse getGradesForStudents(Long courseId, Long studentId) {

        Grade grade = gradeRepo.findByCourseIdAndStudentId(courseId, studentId)
                .orElseThrow(() -> new RuntimeException("Aún no tienes notas en este curso."));

        return new GradeResponse(
                grade.getId(),
                grade.getActivities(),
                grade.getExams(),
                grade.getCourse().getId(),
                grade.getCourse().getName(),
                grade.getStudent().getId(),
                grade.getStudent().getFullName()
        );
    }
    // Obtener las notas  de un curso para el Profesor
    public List<GradeResponse> getGradesForCourse(Long courseId) {
        List<Grade> grades = gradeRepo.findByCourseId(courseId);
        return grades.stream().map(g ->
                new GradeResponse(
                        g.getId(),
                        g.getActivities(),
                        g.getExams(),
                        g.getCourse().getId(),
                        g.getCourse().getName(),
                        g.getStudent().getId(),
                        g.getStudent().getFullName()
                )
        ).toList();
    }


}

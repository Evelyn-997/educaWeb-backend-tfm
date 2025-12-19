package com.tfm.edcuaweb.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.tfm.edcuaweb.dto.*;
import com.tfm.edcuaweb.model.*;
import com.tfm.edcuaweb.repository.CourseNewsRepository;
import com.tfm.edcuaweb.repository.CourseRepository;
import com.tfm.edcuaweb.repository.GradeRepository;
import com.tfm.edcuaweb.repository.UserRepository;
import com.tfm.edcuaweb.service.DocumentService;
import com.tfm.edcuaweb.service.GradeService;
import com.tfm.edcuaweb.service.NotificationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tfm.edcuaweb.service.TeacherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/teacher")
@PreAuthorize("hasRole('TEACHER')")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CourseController {

    private final GradeService gradeService;
    private final TeacherService teacherService;
    private final DocumentService documentService;
    private final NotificationService notificationService;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final CourseNewsRepository courseNewsRepo;



    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> create(
            @Valid @RequestBody CourseCreateRequest request,
            @AuthenticationPrincipal User teacher
    ) {
        String username = teacher.getUsername(); //Viene de JWT
        return ResponseEntity.ok(teacherService.createCourse(request, username));
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getMyCourses(@AuthenticationPrincipal User teacher) {
        String username = teacher.getUsername();
        return ResponseEntity.ok(teacherService.getAllCoursesByTeacher(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id, @AuthenticationPrincipal User teacher) {
        CourseResponse course = teacherService.getCourseDetails(id, teacher, true);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseResponse> getOne(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean includeStudents,
            @AuthenticationPrincipal User teacher
    ) {
        return ResponseEntity.ok(teacherService.getCourseDetails(id, teacher, includeStudents));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseUpdateRequest request,
            @AuthenticationPrincipal User teacher
    ) {
        Course course = teacherService.getCourseById(id,teacher);
        String message = teacher.getFullName()+" ha actualizado el curso "+course.getName();
        NotificationType type = NotificationType.COURSE_UPDATE;
        notificationService.sendToCourse(course,teacher,"Curso ACTUALIZADO ",message,type);

        return ResponseEntity.ok(teacherService.updateMyCourse(id, request, teacher));

    }

    @DeleteMapping("/delete/course/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User teacher
    ) {
        Course course = teacherService.getCourseById(id,teacher);
        String message = teacher.getFullName()+" ha eliminado el curso "+course.getName();
        NotificationType type = NotificationType.COURSE_DELETE;

        teacherService.deleteMyCourse(id, teacher);
        notificationService.sendToCourse(course,teacher,"Curso eliminado",message,type);

        return ResponseEntity.noContent().build();
    }


    /*---------------------------------- STUDENTS ----------------------------------------------------------------- */
    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<List<StudentSummary>> getStudentsForTeacher(@PathVariable Long courseId) {
        List<StudentSummary> students = teacherService.getStudentsByCourse(courseId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<?> getStudentById(@PathVariable Long studentId) {
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Alumno no encontrado");
        }
        StudentSummary dto = StudentSummary.builder()
                .id(student.getId())
                .username(student.getUsername())
                .studentName(student.getName() + " " + student.getLastName())
                .email(student.getEmail())
                .enrollmentNumber(student.getEnrollmentNumber())
                .role(student.getRole().name())
                .build();

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/courses/{courseId}/students/{studentId}")
    public ResponseEntity<CourseResponse> addStudents(
            @PathVariable Long courseId,
            @Valid AddStudentRequest request
    ) {
        return ResponseEntity.ok(teacherService.addStudents(courseId, request));
    }

    //
    @PostMapping("/courses/{courseId}/students/{studentId}/grades")
    public ResponseEntity<?> saveGrade(@AuthenticationPrincipal User teacher,
                                       @PathVariable Long courseId,
                                       @PathVariable Long studentId,
                                       @RequestBody GradeRequest request) {

        gradeService.saveGrade(courseId, studentId, request);
        // Notificar al alumno
        NotificationRequest notifyReq = new NotificationRequest();
        notifyReq.setTitle("Nueva calificación disponible");
        notifyReq.setMessage(teacher.getFullName() + " ha registrado una nueva nota en el curso.");
        notifyReq.setType(NotificationType.GRADE_UPDATE);
        notifyReq.setUserId(studentId);  // destino: el alumno

        notificationService.sendNotification(teacher, notifyReq);

        return ResponseEntity.ok(Map.of("message", "Calificación guardada"));
    }


    @DeleteMapping("/courses/{courseId}/students/{studentId}")
    public ResponseEntity<CourseResponse> removeStudentFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal User teacher
    ) {
        return ResponseEntity.ok(teacherService.removeStudent(courseId, studentId, teacher));
    }

    /*-------------------------DOCUMENTS--------------------------------------------------------------------------*/
    /* Obtener los docuemntos  */
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<List<Document>> getDocuments(@PathVariable Long courseId) {
        return ResponseEntity.ok(documentService.getDocumentsByCourse(courseId));
    }

    /* Filtros dinamicos*/
    @GetMapping("/courses/{courseId}/filter")
    public ResponseEntity<List<Document>> filterDocuments(
            @PathVariable Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String vesion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(documentService.filterDocuments(courseId, type, vesion, from, to));
    }

    /* ------------------------NOTIFICACIONES/NOVEDADES---------------------------*/
    @PostMapping("/courses/{courseId}/news")
    public ResponseEntity<?> addCourseNews(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User teacher,
            @RequestBody Map<String, String> body
    ) {
        String text = body.get("text");
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("La novedad no puede estar vacía");
        }

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        // Guardar novedad
        CourseNews news = CourseNews.builder()
                .text(text)
                .course(course)
                .teacher(teacher)
                .createdAt(LocalDateTime.now())
                .build();
        courseNewsRepo.save(news);
        //Enviar notificacion
        notificationService.sendToCourse(
                course,
                teacher,
                "Nueva novedad en "+ course.getName()+":",
                text,
                NotificationType.COURSE_NEWS
        );
        return ResponseEntity.ok(Map.of("message", "Novedad publicada"));
    }

    /* Novedades*/
    @GetMapping("/course/{courseId}/news")
    public ResponseEntity<List<CourseNewResponse>> getMyNews(@AuthenticationPrincipal User teacher,
                                                             @PathVariable Long courseId){
        return ResponseEntity.ok(
                courseNewsRepo.findByTeacherAndCourse(teacher.getId(), courseId)
                        .stream().map(CourseNewResponse::fromEntity)
                        .toList()
        );
    }

}

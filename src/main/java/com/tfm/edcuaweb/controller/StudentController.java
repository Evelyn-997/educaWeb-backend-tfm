package com.tfm.edcuaweb.controller;

import com.tfm.edcuaweb.dto.CourseNewResponse;
import com.tfm.edcuaweb.dto.CourseResponse;
import com.tfm.edcuaweb.dto.CourseStudentResponse;
import com.tfm.edcuaweb.dto.StudentProgressResponse;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.repository.CourseNewsRepository;
import com.tfm.edcuaweb.repository.CourseRepository;
import com.tfm.edcuaweb.service.AuthService;
import com.tfm.edcuaweb.service.DocumentService;
import com.tfm.edcuaweb.service.StudentService;
import com.tfm.edcuaweb.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class StudentController {
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseNewsRepository courseNewsRepo;

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getMyCourses(@AuthenticationPrincipal User student){
        String username = student.getUsername();
        return ResponseEntity.ok(studentService.getMyCourses(username));
    }

    @GetMapping("/course/{id}/details")
    public ResponseEntity<CourseResponse> getMyCourseDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(studentService.getMyCourseById(id, student.getUsername())
        );
    }
    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseStudentResponse> getCourseForStudent(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User student) {

        return ResponseEntity.ok(
                teacherService.getCourseForStudent(courseId, student)
        );
    }

    @GetMapping("/courses/{courseId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentProgressResponse> getCourseProgress(@AuthenticationPrincipal User student,
                                                                     @PathVariable Long courseId) {
        return ResponseEntity.ok(
                studentService.getStudentProgress(student,courseId)
        );
    }
    /*  Progreso academico  */
    @GetMapping("/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<Long, StudentProgressResponse>> getAllProgress(@AuthenticationPrincipal User student) {
        return ResponseEntity.ok(
                studentService.getAllProgress(student)
        );
    }
    /* Novedades*/
    @GetMapping("/course/{courseId}/news")
    public ResponseEntity<List<CourseNewResponse>> getMyNews(@AuthenticationPrincipal User student,
            @PathVariable Long courseId){
        return ResponseEntity.ok(
                courseNewsRepo.findByStudentAndCourse(student.getId(), courseId)
                        .stream().map(CourseNewResponse::fromEntity)
                        .toList()
        );
    }
}

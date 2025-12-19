package com.tfm.edcuaweb.controller;

import com.tfm.edcuaweb.dto.GradeResponse;
import com.tfm.edcuaweb.model.Grade;
import com.tfm.edcuaweb.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class GradeController {
    private final GradeService gradeService;

    @GetMapping("/courses/{courseId}/students/{studentId}/grades")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GradeResponse> getGrades(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {

        GradeResponse response = gradeService.getGradesForStudents(courseId, studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/courses/{courseId}/grades")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<GradeResponse>> getGradesForCurse(
            @PathVariable Long courseId) {
        List<GradeResponse> grades = gradeService.getGradesForCourse(courseId);
        return ResponseEntity.ok(grades);


    }

}

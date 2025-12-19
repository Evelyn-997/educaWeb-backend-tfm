package com.tfm.edcuaweb.dto;

import com.tfm.edcuaweb.model.ActivityGrade;
import com.tfm.edcuaweb.model.ExamGrades;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeResponse {

    private Long gradeId;
    private List<ActivityGrade> activities;
    private List<ExamGrades> exams;

    private Long courseId;
    private String courseName;

    private Long studentId;
    private String studentName;
}

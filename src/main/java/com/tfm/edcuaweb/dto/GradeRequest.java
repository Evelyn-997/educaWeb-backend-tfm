package com.tfm.edcuaweb.dto;

import com.tfm.edcuaweb.model.ActivityGrade;
import com.tfm.edcuaweb.model.ExamGrades;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
public class GradeRequest {
    private List<ActivityGrade> activities;
    private List<ExamGrades> exams;
}

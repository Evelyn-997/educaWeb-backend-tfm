package com.tfm.edcuaweb.dto;

import com.tfm.edcuaweb.model.ActivityGrade;
import com.tfm.edcuaweb.model.ExamGrades;
import com.tfm.edcuaweb.model.Grade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProgressResponse {
    private Double  gradeAverage;
    private List<ActivityGrade> activityGrades;
    private List<ExamGrades> examGrades;
}

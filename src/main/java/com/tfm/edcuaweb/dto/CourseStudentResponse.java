package com.tfm.edcuaweb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseStudentResponse {
    private Long id;
    private String name;
    private String description;
    private String code;
    private Long teacherId;
    private String teacherName;
    private List<StudentSummary> students;


}

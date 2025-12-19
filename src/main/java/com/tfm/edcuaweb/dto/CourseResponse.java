package com.tfm.edcuaweb.dto;

import java.time.Instant;
import java.util.List;

import com.tfm.edcuaweb.model.User;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CourseResponse {
	private Long id;
	private String name;
	private String description;
	private String code;
	private Long teacherId;
	private String teacherUsername;
    private String teacherName;
	private Instant createdAt;
	private int studentsCount;
	private List<StudentSummary> students;


}

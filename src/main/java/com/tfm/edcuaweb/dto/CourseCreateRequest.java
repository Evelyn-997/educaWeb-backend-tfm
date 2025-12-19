package com.tfm.edcuaweb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CourseCreateRequest {
	@NotBlank private String name;
	private String description;
	@NotBlank private String code;
    private List<StudentSummary> students;
	
}

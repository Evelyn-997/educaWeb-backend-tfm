package com.tfm.edcuaweb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CourseUpdateRequest {
	private String name;
	private String description;
    @NotBlank
    private String code;
    private List<StudentSummary> students;

	  
}

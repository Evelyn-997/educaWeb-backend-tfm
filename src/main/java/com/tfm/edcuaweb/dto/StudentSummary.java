package com.tfm.edcuaweb.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class StudentSummary {
	 private Long id;
	 private String username;
	 private String studentName;
	 private String email;
     private String enrollmentNumber;
     private String role;

}

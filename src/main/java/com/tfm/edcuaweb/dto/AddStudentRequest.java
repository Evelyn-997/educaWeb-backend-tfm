package com.tfm.edcuaweb.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddStudentRequest {
	// admite IDs o usernames; usa uno de los dos
    private List<Long> studentId;
    private List<String> usernames;
	
    public List<Long> getStudentId() {
		return studentId;
	}

    public void setStudentIds(List<Long> studentIds) {
		this.studentId = studentIds;
	}

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    @NotEmpty(message = "Debes enviar al menos un estudiante.")
    public List<?> anyList() {
        return (studentId != null && !studentId.isEmpty()) ? studentId : usernames;
    }

}

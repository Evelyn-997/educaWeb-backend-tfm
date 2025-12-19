package com.tfm.edcuaweb.dto;

import com.tfm.edcuaweb.model.CourseNews;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CourseNewResponse {
    private Long id;
    private String text;
    private String courseName;
    private LocalDateTime createdAt;

    public static CourseNewResponse fromEntity(CourseNews n) {
        return new CourseNewResponse(
                n.getId(),
                n.getText(),
                n.getCourse().getName(),
                n.getCreatedAt()
        );
    }
}

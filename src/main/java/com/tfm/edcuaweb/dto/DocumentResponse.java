package com.tfm.edcuaweb.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
    private Long id;
    private String name;
    private String filePath;
    private String version; //Control de versiones
    private LocalDate uploadDate;
    private String type;
}

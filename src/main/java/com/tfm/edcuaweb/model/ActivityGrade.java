package com.tfm.edcuaweb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;



@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityGrade {
    private String name;
    @Column(precision = 4, scale = 2)
    private BigDecimal grade;
}

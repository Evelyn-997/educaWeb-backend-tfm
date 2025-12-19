package com.tfm.edcuaweb.repository;

import com.tfm.edcuaweb.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByCourseIdAndStudentId(Long courseId, Long studentId);
    List<Grade> findByCourseId(Long courseId);
    List<Grade> findAllByStudentId(Long studentId);

}

package com.tfm.edcuaweb.repository;

import com.tfm.edcuaweb.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long> {
    List<Document> findByCourseId(Long courseId);
    int countByNameAndCourse_Id(String fileName, Long courseId);
    //Filtros combinados
    List<Document> findByCourse_IdAndTypeContainingIgnoreCase(Long courseId, String type);
    List<Document> findByCourse_IdAndVersionContainingIgnoreCase(Long courseId, String version);
    //Filtros por fecha
    @Query("SELECT d FROM Document d WHERE d.course.id = :courseId AND d.uploadDate BETWEEN :from AND :to")
    List<Document> findByCourseAndDateRange(Long courseId, LocalDateTime from, LocalDateTime to);
}

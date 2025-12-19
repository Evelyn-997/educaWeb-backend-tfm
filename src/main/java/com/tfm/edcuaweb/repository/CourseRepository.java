package com.tfm.edcuaweb.repository;

import java.util.List;
import java.util.Optional;

import com.tfm.edcuaweb.model.Course;
import com.tfm.edcuaweb.model.User;

import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	boolean existsByCode(String code);
	Optional<Course> findById(Long id);
    Optional<Course> findByIdAndTeacherId(Long id,Long teacherId);
	Optional<Course> findByIdAndTeacher(@Param("id") Long id, @Param("teacher") User teacher);
    List<Course> findByTeacherId(Long teacherId);
    //usamos una Query porque SEs la forma correcta para relaciones ManyToMany o OneToMany
    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findAllByStudentId(Long studentId);
    List<Course> findByTeacherOrderByCreatedAtDesc(User teacher);

}

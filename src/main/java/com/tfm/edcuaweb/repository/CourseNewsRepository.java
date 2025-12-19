package com.tfm.edcuaweb.repository;

import com.tfm.edcuaweb.model.CourseNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CourseNewsRepository extends JpaRepository<CourseNews,Long> {
    @Query("""
        select n from CourseNews n
        join n.course c
        join c.students s
        where s.id = :studentId
        order by n.createdAt desc
    """)
    List<CourseNews> findAllForStudent(@Param("studentId") Long studentId);
    @Query("""
        select n from CourseNews n
        join n.course c
        join c.students s
        where s.id = :studentId
        and c.id = :courseId
        order by n.createdAt desc
    """)
    List<CourseNews> findByStudentAndCourse(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId
    );
    @Query("""
    select n from CourseNews n
    join n.course c
    where c.id = :courseId
    and n.teacher.id = :teacherId
    order by n.createdAt desc
""")
    List<CourseNews> findByTeacherAndCourse(
            @Param("teacherId") Long teacherId,
            @Param("courseId") Long courseId
    );



}

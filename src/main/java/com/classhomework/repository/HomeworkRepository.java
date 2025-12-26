package com.classhomework.repository;

import com.classhomework.entity.Homework;
import com.classhomework.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findByTeacher(User teacher);
    List<Homework> findByStatus(Homework.HomeworkStatus status);
    List<Homework> findByTeacherId(Long teacherId);

    long countByTeacherId(Long teacherId);

    long countByTeacherIdAndCreatedTimeAfter(Long teacherId, LocalDateTime startOfWeek);

    // 添加自定义JPQL查询，解决跨实体统计问题
    @Query("SELECT COUNT(h) FROM Homework h " +
            "WHERE h.status = com.classhomework.entity.Homework.HomeworkStatus.ACTIVE " +
            "AND h.deadline > :now " +
            "AND NOT EXISTS (" +
            "SELECT s FROM Submission s " +
            "WHERE s.homework.id = h.id " +
            "AND s.student.id = :studentId" +
            ")")
    long countActiveHomeworksNotSubmitted(
            @Param("studentId") Long studentId,
            @Param("now") LocalDateTime now
    );

    boolean existsByIdAndTeacherId(Long homeworkId, Long teacherId);
}
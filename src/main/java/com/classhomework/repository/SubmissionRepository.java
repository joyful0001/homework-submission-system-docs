package com.classhomework.repository;

import com.classhomework.entity.Homework;
import com.classhomework.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    // 获取学生的所有提交记录
    List<Submission> findByStudentId(Long studentId);

    // 获取特定作业的所有提交记录
    List<Submission> findByHomeworkId(Long homeworkId);

    // 检查学生是否已提交特定作业
    boolean existsByHomeworkIdAndStudentId(Long homeworkId, Long studentId);

    long countByHomeworkTeacherIdAndStatus(Long teacherId, Submission.SubmissionStatus submissionStatus);

    // ====== 修复后的方法 ======

    // 1. 统计教师的所有作业提交情况
    @Query("SELECT h.id, h.title, COUNT(s) as submissionCount " +
            "FROM Homework h LEFT JOIN h.submissions s " +
            "WHERE h.teacher.id = :teacherId " +
            "GROUP BY h.id, h.title")
    List<Object[]> findTeacherSubmissionStats(@Param("teacherId") Long teacherId);

    // 2. 统计特定作业的提交数量
    long countByHomeworkId(Long homeworkId);

    // 3. 统计特定作业且状态的提交数量
    long countByHomeworkIdAndStatus(Long homeworkId, Submission.SubmissionStatus submissionStatus);

    // 4. 通过作业实体统计提交
    long countByHomework(Homework homework);

    // 5. 统计特定教师且状态的提交数量
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.homework.teacher.id = :teacherId AND s.status = :status")
    long countByTeacherIdAndStatus(@Param("teacherId") Long teacherId, @Param("status") Submission.SubmissionStatus status);

    long countByStudentIdAndStatusNot(Long studentId, Submission.SubmissionStatus submissionStatus);

    long countByStudentIdAndStatus(Long studentId, Submission.SubmissionStatus submissionStatus);

    // 6. 学生平均分
    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.student.id = :studentId AND s.status = 'GRADED' AND s.score IS NOT NULL")
    Double findAverageScoreByStudentId(@Param("studentId") Long studentId);

    // 7. 作业平均分
    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.homework.id = :homeworkId AND s.status = 'GRADED' AND s.score IS NOT NULL")
    Double findAverageScoreByHomeworkId(@Param("homeworkId") Long homeworkId);
}
package com.classhomework.repository;

import com.classhomework.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudentId(Long studentId);
    List<Submission> findByHomeworkId(Long homeworkId);
    Optional<Submission> findByHomeworkIdAndStudentId(Long homeworkId, Long studentId);
    List<Submission> findByStatus(Submission.SubmissionStatus status);
}
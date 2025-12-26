package com.classhomework.repository;

import com.classhomework.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    // 获取提交相关的所有文件
    List<File> findBySubmissionId(Long submissionId);

    // 获取作业相关的所有文件
    List<File> findByHomeworkId(Long homeworkId);
}
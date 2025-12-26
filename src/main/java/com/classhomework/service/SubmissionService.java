package com.classhomework.service;

import com.classhomework.entity.Homework;
import com.classhomework.entity.Submission;
import com.classhomework.entity.User;
import com.classhomework.repository.FileRepository;
import com.classhomework.repository.HomeworkRepository;
import com.classhomework.repository.SubmissionRepository;
import com.classhomework.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private UserRepository userRepository;

    // 创建提交
    public Submission createSubmission(Long homeworkId, Long studentId, String content) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查作业是否已过期
        if (homework.isExpired()) {
            throw new RuntimeException("作业已过期，无法提交");
        }

        // 检查是否已提交
        if (submissionRepository.existsByHomeworkIdAndStudentId(homeworkId, studentId)) {
            throw new RuntimeException("已提交过该作业");
        }

        Submission submission = new Submission(homework, student);
        submission.setContent(content);
        return submissionRepository.save(submission);
    }

    // 更新提交内容
    public Submission updateSubmission(Long submissionId, String content, Long studentId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("提交记录不存在"));

        // 检查权限
        if (!submission.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("没有权限修改此提交");
        }

        // 检查作业是否已过期
        if (submission.getHomework().isExpired()) {
            throw new RuntimeException("作业已过期，无法修改");
        }

        submission.setContent(content);
        return submissionRepository.save(submission);
    }

    // 提交作业（从草稿状态变为已提交）
    public Submission submitHomework(Long submissionId, Long studentId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("提交记录不存在"));

        // 检查权限
        if (!submission.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("没有权限提交此作业");
        }

        // 检查作业是否已过期
        if (submission.getHomework().isExpired()) {
            throw new RuntimeException("作业已过期，无法提交");
        }

        submission.submit();
        return submissionRepository.save(submission);
    }

    // 教师评分
    public Submission gradeSubmission(Long submissionId, Integer score, String comment, Long teacherId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("提交记录不存在"));

        // 检查权限（只能评阅自己发布的作业）
        if (!submission.getHomework().getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("没有权限评分此提交");
        }

        submission.grade(score, comment);
        return submissionRepository.save(submission);
    }

    // 获取学生的所有提交记录
    public List<Submission> getStudentSubmissions(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    // 获取作业的所有提交记录
    public List<Submission> getHomeworkSubmissions(Long homeworkId, Long teacherId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        // 检查权限
        if (!homework.getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("没有权限查看此作业的提交记录");
        }

        return submissionRepository.findByHomeworkId(homeworkId);
    }

    // 获取单个提交详情
    public Submission getSubmissionDetail(Long submissionId, Long userId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("提交记录不存在"));

        // 检查权限（学生只能看自己的，教师只能看自己作业的）
        if (!submission.getStudent().getId().equals(userId) &&
                !submission.getHomework().getTeacher().getId().equals(userId)) {
            throw new RuntimeException("没有权限查看此提交");
        }

        return submission;
    }
}
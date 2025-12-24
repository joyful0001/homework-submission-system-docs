package com.classhomework.service;

import com.classhomework.entity.Submission;
import com.classhomework.repository.HomeworkRepository;
import com.classhomework.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    // 教师仪表盘数据
    public Map<String, Object> getTeacherDashboard(Long teacherId) {
        Map<String, Object> dashboardData = new HashMap<>();

        // 总作业数
        long totalHomeworks = homeworkRepository.countByTeacherId(teacherId);

        // 本周发布的作业数
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
        long weeklyHomeworks = homeworkRepository.countByTeacherIdAndCreatedTimeAfter(teacherId, startOfWeek);

        // 待批改作业数（使用修复后的方法）
        long pendingGrades = submissionRepository.countByTeacherIdAndStatus(teacherId, Submission.SubmissionStatus.SUBMITTED);

        // 各作业提交情况（使用修复后的方法）
        List<Object[]> submissionStats = submissionRepository.findTeacherSubmissionStats(teacherId);

        dashboardData.put("totalHomeworks", totalHomeworks);
        dashboardData.put("weeklyHomeworks", weeklyHomeworks);
        dashboardData.put("pendingGrades", pendingGrades);
        dashboardData.put("submissionStats", submissionStats);

        return dashboardData;
    }

    // 学生仪表盘数据
    public Map<String, Object> getStudentDashboard(Long studentId) {
        Map<String, Object> dashboardData = new HashMap<>();

        // 待提交作业数
        LocalDateTime now = LocalDateTime.now();
        long pendingSubmissions = homeworkRepository.countActiveHomeworksNotSubmitted(studentId, now);

        // 已提交作业数
        long submittedHomeworks = submissionRepository.countByStudentIdAndStatusNot(studentId, Submission.SubmissionStatus.DRAFT);

        // 已评分作业数
        long gradedHomeworks = submissionRepository.countByStudentIdAndStatus(studentId, Submission.SubmissionStatus.GRADED);

        // 平均分（使用修复后的方法）
        Double averageScore = submissionRepository.findAverageScoreByStudentId(studentId);

        dashboardData.put("pendingSubmissions", pendingSubmissions);
        dashboardData.put("submittedHomeworks", submittedHomeworks);
        dashboardData.put("gradedHomeworks", gradedHomeworks);
        dashboardData.put("averageScore", averageScore != null ? averageScore : 0);

        return dashboardData;
    }

    // 作业提交情况统计
    public Map<String, Object> getHomeworkSubmissionStats(Long homeworkId, Long teacherId) {
        // 验证权限（确保是教师自己的作业）
        if (!homeworkRepository.existsByIdAndTeacherId(homeworkId, teacherId)) {
            throw new RuntimeException("没有权限查看此作业的统计数据");
        }

        Map<String, Object> stats = new HashMap<>();

        // 总提交数
        long totalSubmissions = submissionRepository.countByHomeworkId(homeworkId);

        // 已评分
        long gradedSubmissions = submissionRepository.countByHomeworkIdAndStatus(homeworkId, Submission.SubmissionStatus.GRADED);

        // 未评分
        long ungradedSubmissions = submissionRepository.countByHomeworkIdAndStatus(homeworkId, Submission.SubmissionStatus.SUBMITTED);

        // 平均分（使用修复后的方法）
        Double averageScore = submissionRepository.findAverageScoreByHomeworkId(homeworkId);

        stats.put("totalSubmissions", totalSubmissions);
        stats.put("gradedSubmissions", gradedSubmissions);
        stats.put("ungradedSubmissions", ungradedSubmissions);
        stats.put("averageScore", averageScore != null ? averageScore : 0);

        return stats;
    }
}
package com.classhomework.controller;

import com.classhomework.common.ApiResponse;
import com.classhomework.entity.Submission;
import com.classhomework.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    // 创建提交（草稿）
    @PostMapping
    public ApiResponse<Submission> createSubmission(@RequestBody Map<String, Object> request) {
        Long homeworkId = Long.parseLong(request.get("homeworkId").toString());
        Long studentId = Long.parseLong(request.get("studentId").toString());
        String content = request.get("content").toString();

        Submission submission = submissionService.createSubmission(homeworkId, studentId, content);
        return ApiResponse.success(submission);
    }

    // 更新提交内容
    @PutMapping("/{id}")
    public ApiResponse<Submission> updateSubmission(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long studentId = Long.parseLong(request.get("studentId").toString());
        String content = request.get("content").toString();

        Submission submission = submissionService.updateSubmission(id, content, studentId);
        return ApiResponse.success(submission);
    }

    // 提交作业
    @PostMapping("/{id}/submit")
    public ApiResponse<Submission> submitHomework(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long studentId = Long.parseLong(request.get("studentId").toString());

        Submission submission = submissionService.submitHomework(id, studentId);
        return ApiResponse.success(submission);
    }

    // 评分
    @PostMapping("/{id}/grade")
    public ApiResponse<Submission> gradeSubmission(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long teacherId = Long.parseLong(request.get("teacherId").toString());
        Integer score = Integer.parseInt(request.get("score").toString());
        String comment = request.get("comment").toString();

        Submission submission = submissionService.gradeSubmission(id, score, comment, teacherId);
        return ApiResponse.success(submission);
    }

    // 获取学生的提交记录
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Submission>> getStudentSubmissions(@PathVariable Long studentId) {
        List<Submission> submissions = submissionService.getStudentSubmissions(studentId);
        return ApiResponse.success(submissions);
    }

    // 获取作业的提交记录
    @GetMapping("/homework/{homeworkId}")
    public ApiResponse<List<Submission>> getHomeworkSubmissions(
            @PathVariable Long homeworkId,
            @RequestParam Long teacherId) {
        List<Submission> submissions = submissionService.getHomeworkSubmissions(homeworkId, teacherId);
        return ApiResponse.success(submissions);
    }

    // 获取提交详情
    @GetMapping("/{id}")
    public ApiResponse<Submission> getSubmissionDetail(
            @PathVariable Long id,
            @RequestParam Long userId) {
        Submission submission = submissionService.getSubmissionDetail(id, userId);
        return ApiResponse.success(submission);
    }
}
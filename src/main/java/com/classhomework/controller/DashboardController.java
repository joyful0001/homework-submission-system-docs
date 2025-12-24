package com.classhomework.controller;

import com.classhomework.common.ApiResponse;
import com.classhomework.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // 获取教师仪表盘数据
    @GetMapping("/teacher")
    public ApiResponse<Map<String, Object>> getTeacherDashboard(@RequestParam Long teacherId) {
        Map<String, Object> data = dashboardService.getTeacherDashboard(teacherId);
        return ApiResponse.success(data);
    }

    // 获取学生仪表盘数据
    @GetMapping("/student")
    public ApiResponse<Map<String, Object>> getStudentDashboard(@RequestParam Long studentId) {
        Map<String, Object> data = dashboardService.getStudentDashboard(studentId);
        return ApiResponse.success(data);
    }

    // 获取作业提交情况统计
    @GetMapping("/homework/{homeworkId}")
    public ApiResponse<Map<String, Object>> getHomeworkSubmissionStats(
            @PathVariable Long homeworkId,
            @RequestParam Long teacherId) {
        Map<String, Object> stats = dashboardService.getHomeworkSubmissionStats(homeworkId, teacherId);
        return ApiResponse.success(stats);
    }
}
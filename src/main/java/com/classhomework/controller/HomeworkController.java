package com.classhomework.controller;

import com.classhomework.entity.Homework;
import com.classhomework.service.HomeworkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/homeworks")
// 改用构造器注入（解决IDEA提示的"不建议使用字段注入"问题）
public class HomeworkController {

    // 私有final字段 + 构造器注入（规范写法）
    private final HomeworkService homeworkService;

    // 构造器注入（替代@Autowired字段注入）
    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    // 创建作业（教师）
    @PostMapping
    public ResponseEntity<Homework> createHomework(@RequestBody Homework homework, Authentication authentication) {
        String username = authentication.getName();
        Homework createdHomework = homeworkService.createHomework(homework, username);
        return ResponseEntity.ok(createdHomework);
    }

    // 查询教师发布的作业
    @GetMapping("/teacher")
    public ResponseEntity<List<Homework>> getTeacherHomeworks(Authentication authentication) {
        String username = authentication.getName();
        List<Homework> homeworks = homeworkService.getHomeworksByTeacher(username);
        return ResponseEntity.ok(homeworks);
    }

    // 查询学生可提交的作业
    @GetMapping("/student")
    public ResponseEntity<List<Homework>> getStudentHomeworks() {
        List<Homework> homeworks = homeworkService.getActiveHomeworksForStudents();
        return ResponseEntity.ok(homeworks);
    }

    // 作业详情
    @GetMapping("/{id}")
    public ResponseEntity<Homework> getHomeworkDetails(@PathVariable Long id) {
        Homework homework = homeworkService.getHomeworkById(id);
        return ResponseEntity.ok(homework);
    }

    // 更新作业（修复：传递username参数，解决变量未使用问题）
    @PutMapping("/{id}")
    public ResponseEntity<Homework> updateHomework(
            @PathVariable Long id,
            @RequestBody Homework homework,
            Authentication authentication) {
        String username = authentication.getName();
        // 关键：调用updateHomework时传入username，解决变量未使用 + 方法参数缺失问题
        Homework updatedHomework = homeworkService.updateHomework(id, homework, username);
        return ResponseEntity.ok(updatedHomework);
    }

    // 补充：删除作业接口（匹配需求）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHomework(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        homeworkService.deleteHomework(id, username);
        return ResponseEntity.noContent().build();
    }
}
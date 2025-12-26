package com.classhomework.service.impl;

import com.classhomework.entity.Homework;
import com.classhomework.entity.User;
import com.classhomework.repository.HomeworkRepository;
import com.classhomework.repository.UserRepository;
import com.classhomework.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

// 必须加@Service，让Spring扫描并创建Bean，解决自动装配问题
@Service
public class HomeworkServiceImpl implements HomeworkService {

    // 先用字段注入（兼容你的习惯，后续可改构造器注入）
    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Homework createHomework(Homework homework, String username) {
        // 基础逻辑：根据用户名查教师，关联作业后保存
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("教师不存在"));
        homework.setTeacher(teacher); // 需确保Homework实体有teacher字段
        return homeworkRepository.save(homework);
    }

    @Override
    public List<Homework> getHomeworksByTeacher(String username) {
        // 基础逻辑：查询该教师发布的所有作业
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("教师不存在"));
        return homeworkRepository.findByTeacher(teacher); // 需确保HomeworkRepository有该方法
    }

    @Override
    public List<Homework> getActiveHomeworksForStudents() {
        // 基础逻辑：返回所有未截止的作业（后续可补充截止时间判断）
        return homeworkRepository.findAll();
    }

    @Override
    public Homework getHomeworkById(Long id) {
        // 基础逻辑：根据ID查作业，不存在则抛异常
        return homeworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("作业不存在"));
    }

    @Override
    public Homework updateHomework(Long id, Homework homework, String username) {
        // 基础逻辑：验证教师身份 + 更新作业
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("教师不存在"));
        Homework existingHomework = getHomeworkById(id);

        // 验证作业归属（只有发布者能更新）
        if (!existingHomework.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("无权限更新该作业");
        }

        // 更新字段（根据实际需求调整）
        existingHomework.setTitle(homework.getTitle());
        existingHomework.setContent(homework.getContent());
        existingHomework.setDeadline(homework.getDeadline());
        return homeworkRepository.save(existingHomework);
    }

    @Override
    public void deleteHomework(Long id, String username) {
        // 基础逻辑：验证教师身份 + 删除作业
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("教师不存在"));
        Homework homework = getHomeworkById(id);

        if (!homework.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("无权限删除该作业");
        }

        homeworkRepository.delete(homework);
    }
}
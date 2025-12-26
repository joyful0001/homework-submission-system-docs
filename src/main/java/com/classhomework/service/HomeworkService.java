package com.classhomework.service;

import com.classhomework.entity.Homework;
import java.util.List;

// 作业服务接口，定义所有作业相关方法
public interface HomeworkService {
    // 创建作业（参数：作业对象、创建者用户名）
    Homework createHomework(Homework homework, String username);

    // 查询教师发布的作业（参数：教师用户名）
    List<Homework> getHomeworksByTeacher(String username);

    // 查询学生可提交的作业
    List<Homework> getActiveHomeworksForStudents();

    // 根据ID查询作业详情
    Homework getHomeworkById(Long id);

    // 更新作业（参数：作业ID、新作业对象、操作教师用户名）
    Homework updateHomework(Long id, Homework homework, String username);

    // 扩展：删除作业（补充需求中的删除接口）
    void deleteHomework(Long id, String username);
}
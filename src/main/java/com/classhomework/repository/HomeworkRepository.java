package com.classhomework.repository;

import com.classhomework.entity.Homework;
import com.classhomework.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findByTeacher(User teacher);
    List<Homework> findByStatus(Homework.HomeworkStatus status);
    List<Homework> findByTeacherId(Long teacherId);
}
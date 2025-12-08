package com.classhomework.controller;

import com.classhomework.entity.TestEntity;
import com.classhomework.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestRepository testRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, 班级作业提交系统已启动！";
    }

    @PostMapping("/entity")
    public TestEntity createTestEntity(@RequestBody TestEntity testEntity) {
        testEntity.setCreatedTime(LocalDateTime.now());
        testEntity.setUpdatedTime(LocalDateTime.now());
        return testRepository.save(testEntity);
    }

    @GetMapping("/entities")
    public List<TestEntity> getAllTestEntities() {
        return testRepository.findAll();
    }
}
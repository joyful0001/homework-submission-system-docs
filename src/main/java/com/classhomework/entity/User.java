package com.classhomework.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 核心：确保User是顶级类，移除内部嵌套的User类
@Data // Lombok自动生成getter/setter/toString等，替代所有手动getter/setter
@Entity
@Table(name = "users") // 统一表名，避免和内置关键字冲突
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(unique = true)
    private String email;

    private String phone;

    // 只保留枚举类型的role字段（删除重复的String类型role）
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role; // 统一用UserRole枚举，避免类型冲突

    @Column(name = "class_name", length = 100)
    private String className;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    // 关系映射
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Homework> assignedHomeworks = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>();

    @OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> uploadedFiles = new ArrayList<>();

    // 角色枚举（统一命名为UserRole，避免和其他枚举冲突）
    public enum UserRole {
        STUDENT, TEACHER
    }

    // 构造方法
    public User() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    public User(String username, String password, String name, UserRole role) {
        this();
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role; // 这里赋值枚举类型，不再有类型冲突
    }

    // 预持久化/更新方法保留
    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }

    // 【关键】删除所有手动写的getter/setter/toString
    // 因为@Data注解会自动生成，手动写会冲突；若需要自定义某个getter，再单独写
}
package com.classhomework.service;

import com.classhomework.entity.File;
import com.classhomework.entity.Homework;
import com.classhomework.entity.Submission;
import com.classhomework.entity.User;
import com.classhomework.repository.FileRepository;
import com.classhomework.repository.HomeworkRepository;
import com.classhomework.repository.SubmissionRepository;
import com.classhomework.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private UserRepository userRepository;

    private Path uploadPath;

    // 修复：使用 @PostConstruct 在依赖注入完成后初始化
    @PostConstruct
    public void init() {
        try {
            // 如果配置中没有指定上传目录，使用默认值
            if (uploadDir == null || uploadDir.isEmpty()) {
                uploadDir = System.getProperty("user.dir") + "/uploads";
            }

            this.uploadPath = Paths.get(uploadDir);
            if (!Files.exists(this.uploadPath)) {
                Files.createDirectories(this.uploadPath);
                System.out.println("文件上传目录创建成功: " + this.uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + uploadDir, e);
        }
    }

    // 上传提交相关文件
    public File uploadSubmissionFile(MultipartFile file, Long submissionId, Long userId) throws IOException {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("提交记录不存在"));

        // 检查权限
        if (!submission.getStudent().getId().equals(userId)) {
            throw new RuntimeException("没有权限上传文件");
        }

        // 检查文件大小和类型
        validateFile(file);

        // 保存文件
        String fileName = saveFile(file);

        // 创建文件记录
        File fileEntity = new File();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFilePath(fileName);
        fileEntity.setFileSize(file.getSize());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setSubmission(submission);

        return fileRepository.save(fileEntity);
    }

    // 上传作业相关文件
    public File uploadHomeworkFile(MultipartFile file, Long homeworkId, Long teacherId) throws IOException {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        // 检查权限
        if (!homework.getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("没有权限上传文件");
        }

        // 检查文件大小和类型
        validateFile(file);

        // 保存文件
        String fileName = saveFile(file);

        // 创建文件记录
        File fileEntity = new File();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFilePath(fileName);
        fileEntity.setFileSize(file.getSize());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setHomework(homework);

        return fileRepository.save(fileEntity);
    }

    // 下载文件
    public Path getFile(Long fileId, Long userId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));

        // 检查权限
        boolean hasPermission = false;

        // 如果是提交相关文件
        if (file.getSubmission() != null) {
            Submission submission = file.getSubmission();
            // 学生可以下载自己的提交文件
            // 教师可以下载自己作业的提交文件
            hasPermission = submission.getStudent().getId().equals(userId) ||
                    submission.getHomework().getTeacher().getId().equals(userId);
        }

        // 如果是作业相关文件
        if (file.getHomework() != null) {
            Homework homework = file.getHomework();
            // 教师可以下载自己上传的文件
            // 学生可以下载作业相关文件
            hasPermission = homework.getTeacher().getId().equals(userId) ||
                    userRepository.existsById(userId); // 假设所有学生都可以查看作业文件
        }

        if (!hasPermission) {
            throw new RuntimeException("没有权限下载此文件");
        }

        return uploadPath.resolve(file.getFilePath());
    }

    // 删除文件
    public void deleteFile(Long fileId, Long userId) throws IOException {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));

        // 检查权限
        boolean hasPermission = false;

        // 如果是提交相关文件
        if (file.getSubmission() != null) {
            Submission submission = file.getSubmission();
            // 只能删除自己的提交文件，且提交状态为草稿
            hasPermission = submission.getStudent().getId().equals(userId) &&
                    submission.getStatus() == Submission.SubmissionStatus.DRAFT;
        }

        // 如果是作业相关文件
        if (file.getHomework() != null) {
            Homework homework = file.getHomework();
            // 教师可以删除自己上传的文件
            hasPermission = homework.getTeacher().getId().equals(userId);
        }

        if (!hasPermission) {
            throw new RuntimeException("没有权限删除此文件");
        }

        // 删除物理文件
        Path filePath = uploadPath.resolve(file.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // 删除数据库记录
        fileRepository.delete(file);
    }

    // 保存文件到磁盘
    private String saveFile(MultipartFile file) throws IOException {
        // 生成唯一文件名
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path path = uploadPath.resolve(uniqueFileName);
        Files.write(path, file.getBytes());

        return uniqueFileName;
    }

    // 验证文件
    private void validateFile(MultipartFile file) {
        // 检查文件大小（限制10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("文件大小不能超过10MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("application/pdf") &&
                        !contentType.startsWith("application/msword") &&
                        !contentType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml") &&
                        !contentType.startsWith("image/"))) {
            throw new RuntimeException("不支持的文件类型");
        }
    }
}
package com.classhomework.controller;

import com.classhomework.common.ApiResponse;
import com.classhomework.entity.File;
import com.classhomework.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    // 上传提交相关文件
    @PostMapping("/submission")
    public ApiResponse<File> uploadSubmissionFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long submissionId,
            @RequestParam Long userId) {
        try {
            File uploadedFile = fileService.uploadSubmissionFile(file, submissionId, userId);
            return ApiResponse.success(uploadedFile);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 上传作业相关文件
    @PostMapping("/homework")
    public ApiResponse<File> uploadHomeworkFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long homeworkId,
            @RequestParam Long teacherId) {
        try {
            File uploadedFile = fileService.uploadHomeworkFile(file, homeworkId, teacherId);
            return ApiResponse.success(uploadedFile);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 下载文件
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long fileId,
            @RequestParam Long userId) {
        try {
            Path filePath = fileService.getFile(fileId, userId);
            Resource resource = new FileSystemResource(filePath);

            String fileName = resource.getFilename();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    // 删除文件
    @DeleteMapping("/{fileId}")
    public ApiResponse<? extends Object> deleteFile(
            @PathVariable Long fileId,
            @RequestParam Long userId) {
        try {
            fileService.deleteFile(fileId, userId);
            return ApiResponse.success("文件删除成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
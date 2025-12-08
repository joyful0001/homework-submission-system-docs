package com.classhomework.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "files")
public class File {

    @Id
    @Column(name = "file_id", length = 50)
    private String fileId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "original_name", length = 255)
    private String originalName;

    // 构造方法
    public File() {
        this.fileId = generateFileId();
        this.uploadTime = LocalDateTime.now();
    }

    public File(String fileName, String filePath, Long fileSize, User uploader, String mimeType) {
        this();
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.uploader = uploader;
        this.mimeType = mimeType;
        this.originalName = fileName;
    }

    // 生成文件ID的私有方法
    private String generateFileId() {
        // 方案1: 使用UUID（推荐，更短）
        return "FILE_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        // 方案2: 使用时间戳（备用）
        // return "FILE_" + System.currentTimeMillis();

        // 方案3: 使用格式化时间（如果DateTimeFormatter有问题就用上面的方案）
        // try {
        //     return "FILE_" + LocalDateTime.now().format(
        //         DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")
        //     );
        // } catch (Exception e) {
        //     return "FILE_" + System.currentTimeMillis();
        // }
    }

    // 辅助方法：获取文件扩展名
    public String getFileExtension() {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    // 辅助方法：获取文件大小（带单位）
    public String getFormattedFileSize() {
        if (fileSize == null) return "0 B";

        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    // Getter和Setter方法
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    @Override
    public String toString() {
        return "File{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", uploaderId=" + (uploader != null ? uploader.getId() : null) +
                ", uploadTime=" + uploadTime +
                ", mimeType='" + mimeType + '\'' +
                ", originalName='" + originalName + '\'' +
                '}';
    }
}
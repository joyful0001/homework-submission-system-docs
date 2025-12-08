package com.classhomework.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileUtil {

    // 文件上传路径（开发环境）
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * 保存上传的文件
     * @param file 上传的文件
     * @param subDir 子目录，如 "homework", "submission"
     * @return 保存后的文件路径
     */
    public static String saveFile(MultipartFile file, String subDir) throws IOException {
        // 创建目录
        String dirPath = UPLOAD_DIR + subDir + "/";
        Path dir = Paths.get(dirPath);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = dir.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        return dirPath + filename;
    }

    /**
     * 生成时间戳文件名
     */
    public static String generateTimestampFilename(String originalFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        if (originalFilename == null) {
            return timestamp;
        }
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return timestamp + extension;
    }
}

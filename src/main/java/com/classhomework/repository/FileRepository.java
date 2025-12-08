package com.classhomework.repository;

import com.classhomework.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
    List<File> findByUploaderId(Long uploaderId);
    List<File> findByFileNameContaining(String keyword);
    List<File> findByMimeType(String mimeType);
}
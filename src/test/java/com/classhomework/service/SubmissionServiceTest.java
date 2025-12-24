package com.classhomework.service;

import com.classhomework.entity.Homework;
import com.classhomework.entity.Submission;
import com.classhomework.entity.User;
import com.classhomework.repository.HomeworkRepository;
import com.classhomework.repository.SubmissionRepository;
import com.classhomework.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private HomeworkRepository homeworkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubmissionService submissionService;

    @Test
    public void testCreateSubmission_Success() {
        // 准备测试数据
        Long homeworkId = 1L;
        Long studentId = 1L;
        String content = "测试提交内容";

        Homework homework = new Homework();
        homework.setId(homeworkId);
        homework.setDeadline(LocalDateTime.now().plusDays(1));

        User student = new User();
        student.setId(studentId);

        when(homeworkRepository.findById(homeworkId)).thenReturn(Optional.of(homework));
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(submissionRepository.existsByHomeworkIdAndStudentId(homeworkId, studentId)).thenReturn(false);
        when(submissionRepository.save(any(Submission.class))).thenAnswer(i -> i.getArgument(0));

        // 执行测试
        Submission result = submissionService.createSubmission(homeworkId, studentId, content);

        // 验证结果
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(Submission.SubmissionStatus.DRAFT, result.getStatus());

        verify(homeworkRepository, times(1)).findById(homeworkId);
        verify(userRepository, times(1)).findById(studentId);
        verify(submissionRepository, times(1)).existsByHomeworkIdAndStudentId(homeworkId, studentId);
        verify(submissionRepository, times(1)).save(any(Submission.class));
    }

    @Test
    public void testCreateSubmission_HomeworkExpired() {
        // 准备测试数据
        Long homeworkId = 1L;
        Long studentId = 1L;

        Homework homework = new Homework();
        homework.setId(homeworkId);
        homework.setDeadline(LocalDateTime.now().minusDays(1));

        when(homeworkRepository.findById(homeworkId)).thenReturn(Optional.of(homework));

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            submissionService.createSubmission(homeworkId, studentId, "测试内容");
        });

        verify(homeworkRepository, times(1)).findById(homeworkId);
        verify(userRepository, times(1)).findById(studentId);
        verify(submissionRepository, never()).save(any());
    }
}
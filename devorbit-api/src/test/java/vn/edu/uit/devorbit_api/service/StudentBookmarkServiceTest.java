package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import vn.edu.uit.devorbit_api.dto.student.StudentBookmarkRequest;
import vn.edu.uit.devorbit_api.entity.StudentBookmark;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.repository.StudentBookmarkRepository;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentBookmarkServiceTest {

    @Mock private StudentBookmarkRepository bookmarkRepository;
    @Mock private StudentUserRepository studentUserRepository;

    @Test
    void addBookmark_checksDuplicateUsingNormalizedTargetType() {
        StudentBookmarkService service = new StudentBookmarkService(bookmarkRepository, studentUserRepository);
        StudentUser student = StudentUser.builder().id(1L).studentCode("24520554").build();
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student));
        when(bookmarkRepository.existsByStudentIdAndTargetTypeAndTargetId(1L, "COURSE", 10L)).thenReturn(true);

        StudentBookmarkRequest request = new StudentBookmarkRequest(" course ", 10L, "Course", null, "/courses/10");

        assertThatThrownBy(() -> service.addBookmark("24520554", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
        verify(bookmarkRepository, never()).save(any());
    }

    @Test
    void addBookmark_savesNormalizedTargetType() {
        StudentBookmarkService service = new StudentBookmarkService(bookmarkRepository, studentUserRepository);
        StudentUser student = StudentUser.builder().id(1L).studentCode("24520554").build();
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student));
        when(bookmarkRepository.save(any())).thenAnswer(invocation -> {
            StudentBookmark bookmark = invocation.getArgument(0);
            bookmark.setId(99L);
            return bookmark;
        });

        StudentBookmarkRequest request = new StudentBookmarkRequest(" repo ", 20L, "Repo", null, "/repos/20");
        service.addBookmark("24520554", request);

        ArgumentCaptor<StudentBookmark> captor = ArgumentCaptor.forClass(StudentBookmark.class);
        verify(bookmarkRepository).save(captor.capture());
        assertThat(captor.getValue().getTargetType()).isEqualTo("REPO");
    }

    @Test
    void addBookmark_translatesConcurrentDuplicate() {
        StudentBookmarkService service = new StudentBookmarkService(bookmarkRepository, studentUserRepository);
        StudentUser student = StudentUser.builder().id(1L).studentCode("24520554").build();
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student));
        when(bookmarkRepository.save(any())).thenThrow(new DataIntegrityViolationException("unique bookmark"));

        StudentBookmarkRequest request = new StudentBookmarkRequest("repo", 20L, "Repo", null, "/repos/20");

        assertThatThrownBy(() -> service.addBookmark("24520554", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }
}

package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.edu.uit.devorbit_api.dto.student.CreateTaskRequest;
import vn.edu.uit.devorbit_api.dto.student.UpdateTaskRequest;
import vn.edu.uit.devorbit_api.entity.PersonalTask;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.repository.PersonalTaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PersonalTaskServiceTest {

    private final PersonalTaskRepository personalTaskRepository = mock(PersonalTaskRepository.class);
    private final PersonalTaskService service = new PersonalTaskService(personalTaskRepository);

    private PersonalTask pastTask;
    private PersonalTask futureTask;
    private PersonalTask recurringPastTask;

    @BeforeEach
    void setUp() {
        pastTask = PersonalTask.builder()
            .id(1L)
            .studentCode("student1")
            .title("Overdue task")
            .deadline(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.NOON))
            .completed(false)
            .recurrence(null)
            .build();

        futureTask = PersonalTask.builder()
            .id(2L)
            .studentCode("student1")
            .title("Future task")
            .deadline(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.NOON))
            .completed(false)
            .recurrence(null)
            .build();

        recurringPastTask = PersonalTask.builder()
            .id(3L)
            .studentCode("student1")
            .title("Recurring past task")
            .deadline(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.NOON))
            .completed(false)
            .recurrence("DAILY")
            .build();
    }

    @Test
    void updateTask_pastDeadline_throws() {
        when(personalTaskRepository.findById(1L)).thenReturn(Optional.of(pastTask));

        assertThatThrownBy(() -> service.updateTask("student1", 1L, new UpdateTaskRequest(
            "New title", null, null, null, null, null, null, null)))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void updateTask_futureDeadline_succeeds() {
        when(personalTaskRepository.findById(2L)).thenReturn(Optional.of(futureTask));
        when(personalTaskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.updateTask("student1", 2L, new UpdateTaskRequest(
            "Updated title", null, null, null, null, null, null, null));
    }

    @Test
    void updateTask_recurringPastDeadline_succeeds() {
        when(personalTaskRepository.findById(3L)).thenReturn(Optional.of(recurringPastTask));
        when(personalTaskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.updateTask("student1", 3L, new UpdateTaskRequest(
            "Updated title", null, null, null, null, null, null, null));
    }

    @Test
    void deleteTask_pastDeadline_throws() {
        when(personalTaskRepository.findById(1L)).thenReturn(Optional.of(pastTask));

        assertThatThrownBy(() -> service.deleteTask("student1", 1L))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void deleteTask_futureDeadline_succeeds() {
        when(personalTaskRepository.findById(2L)).thenReturn(Optional.of(futureTask));

        service.deleteTask("student1", 2L);

        verify(personalTaskRepository).delete(futureTask);
    }

    @Test
    void toggleTask_pastDeadline_throws() {
        when(personalTaskRepository.findById(1L)).thenReturn(Optional.of(pastTask));

        assertThatThrownBy(() -> service.toggleTask("student1", 1L, true))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void toggleTask_futureDeadline_succeeds() {
        when(personalTaskRepository.findById(2L)).thenReturn(Optional.of(futureTask));
        when(personalTaskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.toggleTask("student1", 2L, true);

        verify(personalTaskRepository).save(any());
    }
}

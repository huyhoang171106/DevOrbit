package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.student.CreateTaskRequest;
import vn.edu.uit.devorbit_api.dto.student.TaskResponse;
import vn.edu.uit.devorbit_api.dto.student.UpdateTaskRequest;
import vn.edu.uit.devorbit_api.entity.PersonalTask;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.PersonalTaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalTaskService {

    private static final Logger log = LoggerFactory.getLogger(PersonalTaskService.class);
    private final PersonalTaskRepository personalTaskRepository;

    public List<TaskResponse> getTasks(String studentCode, String filter) {
        List<PersonalTask> tasks;
        if ("today".equalsIgnoreCase(filter)) {
            LocalDate today = LocalDate.now();
            LocalDateTime start = today.atStartOfDay();
            LocalDateTime end = today.plusDays(1).atStartOfDay();
            tasks = personalTaskRepository.findByStudentCodeAndDeadlineBetween(studentCode, start, end);
        } else if ("week".equalsIgnoreCase(filter)) {
            LocalDate now = LocalDate.now();
            LocalDate monday = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDateTime start = monday.atStartOfDay();
            LocalDateTime end = monday.plusDays(7).atStartOfDay();
            tasks = personalTaskRepository.findByStudentCodeAndDeadlineBetween(studentCode, start, end);
        } else {
            tasks = personalTaskRepository.findByStudentCodeOrderByCreatedAtDesc(studentCode);
        }
        return tasks.stream().map(TaskResponse::from).toList();
    }

    @Transactional
    public TaskResponse createTask(String studentCode, CreateTaskRequest request) {
        PersonalTask task = PersonalTask.builder()
            .studentCode(studentCode)
            .title(request.title())
            .description(request.description())
            .deadline(request.deadline())
            .recurrence(request.recurrence())
            .recurrenceDaysOfWeek(request.recurrenceDaysOfWeek())
            .recurrenceStartDate(request.recurrenceStartDate())
            .recurrenceEndDate(request.recurrenceEndDate())
            .build();
        task = personalTaskRepository.save(task);
        log.info("Personal task created: id={}, studentCode={}", task.getId(), studentCode);
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse updateTask(String studentCode, Long taskId, UpdateTaskRequest request) {
        PersonalTask task = findOwnedTask(taskId, studentCode);
        validateNotPastDeadline(task);
        if (request.title() != null) task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.deadline() != null) task.setDeadline(request.deadline());
        if (request.completed() != null) task.setCompleted(request.completed());
        if (request.recurrence() != null) task.setRecurrence(request.recurrence());
        if (request.recurrenceDaysOfWeek() != null) task.setRecurrenceDaysOfWeek(request.recurrenceDaysOfWeek());
        if (request.recurrenceStartDate() != null) task.setRecurrenceStartDate(request.recurrenceStartDate());
        if (request.recurrenceEndDate() != null) task.setRecurrenceEndDate(request.recurrenceEndDate());
        task.setUpdatedAt(LocalDateTime.now());
        task = personalTaskRepository.save(task);
        return TaskResponse.from(task);
    }

    @Transactional
    public void deleteTask(String studentCode, Long taskId) {
        PersonalTask task = findOwnedTask(taskId, studentCode);
        validateNotPastDeadline(task);
        personalTaskRepository.delete(task);
        log.info("Personal task deleted: id={}, studentCode={}", taskId, studentCode);
    }

    @Transactional
    public TaskResponse toggleTask(String studentCode, Long taskId, boolean completed) {
        PersonalTask task = findOwnedTask(taskId, studentCode);
        validateNotPastDeadline(task);
        task.setCompleted(completed);
        task.setUpdatedAt(LocalDateTime.now());
        task = personalTaskRepository.save(task);
        return TaskResponse.from(task);
    }

    private void validateNotPastDeadline(PersonalTask task) {
        if (task.getDeadline() != null
            && task.getDeadline().toLocalDate().isBefore(LocalDate.now())
            && !task.isCompleted()
            && task.getRecurrence() == null) {
            throw new BadRequestException("Cannot modify a past-deadline uncompleted task");
        }
    }

    private PersonalTask findOwnedTask(Long taskId, String studentCode) {
        PersonalTask task = personalTaskRepository.findById(taskId)
            .orElseThrow(() -> new NotFoundException("Task not found"));
        if (!task.getStudentCode().equals(studentCode)) {
            throw new NotFoundException("Task not found");
        }
        return task;
    }
}

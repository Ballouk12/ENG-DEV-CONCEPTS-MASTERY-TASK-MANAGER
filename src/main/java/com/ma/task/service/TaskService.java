package com.ma.task.service;

import com.ma.exception.ResourceNotFoundException;
import com.ma.task.dto.CreateTaskRequest;
import com.ma.task.dto.TaskResponse;
import com.ma.task.dto.UpdateTaskRequest;
import com.ma.task.entity.Task;
import com.ma.task.mapper.TaskMapper;
import com.ma.task.repository.TaskRepository;
import com.ma.user.entity.User;
import com.ma.user.service.UserService;
import jakarta.persistence.OptimisticLockException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper mapper;
    private final UserService userService;

    public TaskService(
            TaskRepository taskRepository,
            TaskMapper mapper,
            UserService userService
    ) {
        this.taskRepository = taskRepository;
        this.mapper= mapper;
        this.userService = userService;
    }

    @Transactional(readOnly=true)
    public Page<TaskResponse> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "tasks", key = "#id")
    @Transactional(readOnly=true)
    public TaskResponse findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("le task avec id : "+ id +" n'existe pas"));
        return mapper.toResponse(task);
    }

    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        User user = userService.getEntityById(request.userId());
        Task task = taskRepository.save(mapper.toEntity(request,user));
        return mapper.toResponse(task);
    }

    @Transactional
    public TaskResponse update(Long id , UpdateTaskRequest request) {
        Task task = taskRepository.findById((id))
                .orElseThrow(() -> new ResourceNotFoundException("le task avec id : "+ id +" n'existe pas"));
        if(!task.getVersion().equals(request.version())) {
            throw new OptimisticLockException(
                    "Task " + id + " has been modified. Expected version " + request.version()
                            + " but was " + task.getVersion()
            );
        }
        task.setTitle(request.title());
        task.setDescription(request.description());
        try {
            Task saved = taskRepository.save(task);
            return mapper.toResponse(saved);
        } catch(ObjectOptimisticLockingFailureException ex) {
            throw new OptimisticLockException("Task " + id + " has been modified concurrently");
        }
    }

    @Transactional
    public void delete(Long id) {
        if(!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task " + id + " not found");
        }
        taskRepository.deleteById(id);
    }
}

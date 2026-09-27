package com.ma.task.mapper;


import com.ma.task.entity.Task;
import com.ma.task.dto.CreateTaskRequest;
import com.ma.task.dto.TaskResponse;
import com.ma.task.enums.TaskStatus;
import com.ma.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(CreateTaskRequest taskRequest, User user) {
        Task task = new Task();
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setStatus(TaskStatus.TODO);
        task.setUser(user);
        return task;
    }

    public TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse(
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getVersion(),
                task.getUser() != null ? task.getUser().getId() : null
        );
    return response;
    }
}

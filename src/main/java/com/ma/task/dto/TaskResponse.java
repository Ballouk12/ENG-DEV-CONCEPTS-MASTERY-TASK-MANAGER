package com.ma.task.dto;

import com.ma.task.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public record TaskResponse(
        String title,
        String descrioption,
        TaskStatus status,
        Instant createdAt,
        Long version,
        Long userId
        ) {
}

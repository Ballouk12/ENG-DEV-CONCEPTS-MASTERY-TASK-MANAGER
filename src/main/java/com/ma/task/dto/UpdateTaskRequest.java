package com.ma.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @NotBlank(message="le titre ne peut pas etre vide")
        @Size(max=150 , message="le message ne peut contenir plus de 150 caracteres")
        String title,
        @Size(max=1000 , message="le message ne peut contenir plus de 1000 caracteres")
        String description,
        @NotNull(message="la version ne peut pas etre null")
        Long version
) {
}

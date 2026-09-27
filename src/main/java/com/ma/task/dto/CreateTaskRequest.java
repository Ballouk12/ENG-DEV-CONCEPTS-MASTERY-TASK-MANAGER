package com.ma.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank(message="le titre ne peut pas etre vide")
        @Size(max=150 , message="le titre ne peut pas etre plus de 15o caracteres")
        String title,
        @Size(max=1000 , message="la description ne peut pas depasser 1000 caractere")
        String description,
        @NotNull(message = "userId est obligatoire : une task doit appartenir à un user")
        Long userId
) { }

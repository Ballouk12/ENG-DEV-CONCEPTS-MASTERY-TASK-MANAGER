package com.ma.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "Le username est obligatoire")
        @Size(max = 100, message = "Le username ne peut pas dépasser 100 caractères")
        String username
) {}
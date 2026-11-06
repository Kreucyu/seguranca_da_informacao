package com.exercise.DTO;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank String login,
        @NotBlank String password
) {
}

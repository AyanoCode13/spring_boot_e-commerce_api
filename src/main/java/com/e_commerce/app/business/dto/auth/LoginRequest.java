package com.e_commerce.app.business.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) { }
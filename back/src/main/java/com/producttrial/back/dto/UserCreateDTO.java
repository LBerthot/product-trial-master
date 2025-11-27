package com.producttrial.back.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(unique = true)
    private String username;

    private String firstname;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).+$",
    message = "Password must contain at least one uppercase letter, one special character and one digit")
    private String password;
}

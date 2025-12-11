package com.producttrial.back.mapper;

import com.producttrial.back.dto.UserCreateDTO;
import com.producttrial.back.dto.UserResponseDTO;
import com.producttrial.back.entity.User;

public class UserMapper {
    private UserMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static User toEntity(UserCreateDTO dto) {
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstname(dto.getFirstname())
                .password(dto.getPassword())
                .build();
    }

    public static UserCreateDTO toDto(User user) {
        return UserCreateDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    public static UserResponseDTO toResponse (User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
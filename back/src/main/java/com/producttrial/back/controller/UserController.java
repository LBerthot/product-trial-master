package com.producttrial.back.controller;

import com.producttrial.back.dto.UserCreateDTO;
import com.producttrial.back.dto.UserResponseDTO;
import com.producttrial.back.entity.User;
import com.producttrial.back.mapper.UserMapper;
import com.producttrial.back.service.iservice.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequestMapping("/account")
@Slf4j
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO userDTO) {
        log.info("POST /account - payload username={}, email={}", userDTO.getUsername(), userDTO.getEmail());
        User created = userService.save(UserMapper.toEntity(userDTO));
        UserResponseDTO resp = UserMapper.toResponse(created);
        URI location = URI.create("/account/" + created.getId());
        return ResponseEntity.created(location).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable @Positive Long id) {
        log.info("GET /account/{}", id);
        User user = userService.findById(id).orElseThrow(() -> {
            log.warn("User not found id={}", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id " + id);
        });
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }
}

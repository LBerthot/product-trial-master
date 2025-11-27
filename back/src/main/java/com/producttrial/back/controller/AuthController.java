package com.producttrial.back.controller;

import com.producttrial.back.dto.AuthRequestDTO;
import com.producttrial.back.dto.AuthResponseDTO;
import com.producttrial.back.entity.User;
import com.producttrial.back.service.iservice.IJwtService;
import com.producttrial.back.service.iservice.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/token")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final IJwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        log.info("POST /token - login attempt for email={}", request.getEmail());

        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.info("POST /token - authentication failed for email={}", request.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId());

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}

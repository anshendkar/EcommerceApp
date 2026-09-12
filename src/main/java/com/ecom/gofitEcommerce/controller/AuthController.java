package com.ecom.gofitEcommerce.controller;

import com.ecom.gofitEcommerce.DTO.AuthRequest;
import com.ecom.gofitEcommerce.DTO.AuthResponse;
import com.ecom.gofitEcommerce.DTO.SignupRequest;
import com.ecom.gofitEcommerce.DTO.UserDto;
import com.ecom.gofitEcommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signupUser(@RequestBody @Valid SignupRequest signupRequest) {
        if (authService.hasUserWithEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>("User already exists", HttpStatus.NOT_ACCEPTABLE);
        }
        UserDto userDto = authService.createUser(signupRequest);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid AuthRequest request) {
        String token = authService.verify(request);
        UserDto userDto = authService.getUserByEmail(request.getEmail());

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", userDto);

        return ResponseEntity.ok(map);
    }
}


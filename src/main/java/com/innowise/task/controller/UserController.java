package com.innowise.task.controller;

import com.innowise.task.dto.ChangePasswordRequest;
import com.innowise.task.dto.CreateUserRequest;
import com.innowise.task.dto.LoginUserRequest;
import com.innowise.task.dto.TokenResponse;
import com.innowise.task.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<TokenResponse> register(@Valid @RequestBody CreateUserRequest createUserDto) {
    TokenResponse response = userService.register(createUserDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginUserRequest loginUserDto) {
    TokenResponse response = userService.login(loginUserDto);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/update/password")
  public ResponseEntity<TokenResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    TokenResponse response = userService.changePassword(changePasswordRequest);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
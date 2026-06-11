package com.innowise.task.service;

import com.innowise.task.dto.ChangePasswordRequest;
import com.innowise.task.dto.CreateUserRequest;
import com.innowise.task.dto.LoginUserRequest;
import com.innowise.task.dto.TokenResponse;

public interface UserService {
  TokenResponse register(CreateUserRequest userDto);

  TokenResponse login(LoginUserRequest loginUserDto);

  TokenResponse changePassword(ChangePasswordRequest changePasswordRequest);
}
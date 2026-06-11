package com.innowise.task.service.impl;

import com.innowise.task.dto.ChangePasswordRequest;
import com.innowise.task.dto.CreateUserRequest;
import com.innowise.task.dto.LoginUserRequest;
import com.innowise.task.dto.TokenResponse;
import com.innowise.task.entity.Role;
import com.innowise.task.entity.User;
import com.innowise.task.exception.AlreadyExistsException;
import com.innowise.task.exception.NotFoundException;
import com.innowise.task.repository.RoleRepository;
import com.innowise.task.repository.UserRepository;
import com.innowise.task.security.jwt.JwtService;
import com.innowise.task.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, JwtService jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
  }

  @Override
  @Transactional
  public TokenResponse register(CreateUserRequest userDto) {
    String username = userDto.getUsername();
    if (userRepository.existsByUsername(username)) {
      throw new AlreadyExistsException("Username is already in use");
    }

    String email = userDto.getEmail();
    if (userRepository.existsByEmail(email)) {
      throw new AlreadyExistsException("Email is already in use");
    }

    String password = userDto.getPassword();
    String passwordHash = passwordEncoder.encode(password);
    Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new NotFoundException("Role not found")); //todo constant
    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPasswordHash(passwordHash);
    user.setRole(userRole);
    try {
      userRepository.save(user);
      SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.getName());
      UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, passwordHash, List.of(authority));
      String token = jwtService.generateJwtToken(userDetails);
      return new TokenResponse(token);
    } catch (DataIntegrityViolationException e) {
      throw new AlreadyExistsException("User already exists");
    }
  }

  @Override
  public TokenResponse login(LoginUserRequest request) {
    String username = request.getUsername();
    String password = request.getPassword();
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    Authentication authentication = authenticationManager.authenticate(authenticationToken);
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String token = jwtService.generateJwtToken(userDetails);
    return new TokenResponse(token);
  }

  @Override
  @Transactional
  public TokenResponse changePassword(ChangePasswordRequest changePasswordRequest) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, changePasswordRequest.getOldPassword());
    Authentication authenticated = authenticationManager.authenticate(authenticationToken);
    UserDetails userDetails = (UserDetails) authenticated.getPrincipal();

    User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
    String newPasswordHash = passwordEncoder.encode(changePasswordRequest.getNewPassword());
    user.setPasswordHash(newPasswordHash);
    userRepository.save(user);

    String token = jwtService.generateJwtToken(userDetails);
    return new TokenResponse(token);
  }
}
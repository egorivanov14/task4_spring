package com.innowise.task.service;

import com.innowise.task.dto.CreateUserRequest;
import com.innowise.task.dto.LoginUserRequest;
import com.innowise.task.dto.TokenResponse;
import com.innowise.task.entity.Role;
import com.innowise.task.entity.User;
import com.innowise.task.repository.RoleRepository;
import com.innowise.task.repository.UserRepository;
import com.innowise.task.security.jwt.JwtService;
import com.innowise.task.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  public void shouldReturnTokenToRegistration() {
    CreateUserRequest createUserRequest = new CreateUserRequest("1", "testmail@gmail.com", "123456789");//todo constant

    Role roleUser = new Role();
    roleUser.setName("ROLE_USER");

    when(userRepository.existsByUsername("1")).thenReturn(false);
    when(userRepository.existsByEmail("testmail@gmail.com")).thenReturn(false);
    when(passwordEncoder.encode("123456789")).thenReturn("encoded_password");
    when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
    when(jwtService.generateJwtToken(any(UserDetails.class))).thenReturn("fake_token");

    TokenResponse response = userService.register(createUserRequest);

    assertThat(response).isNotNull();
    assertThat(response.getAccessToken()).isEqualTo("fake_token");
  }

  @Test
  public void shouldReturnTokenToLogin() {
    LoginUserRequest request = new LoginUserRequest("1", "123456789");

    UserDetails userDetails = mock(UserDetails.class);
    Authentication authenticationMock = mock(Authentication.class);

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticationMock);
    when(authenticationMock.getPrincipal()).thenReturn(userDetails);
    when(jwtService.generateJwtToken(any(UserDetails.class))).thenReturn("fake_token");

    TokenResponse response = userService.login(request);

    assertThat(response).isNotNull();
    assertThat(response.getAccessToken()).isEqualTo("fake_token");
  }


}
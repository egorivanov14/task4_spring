package com.innowise.task.mapper;

import com.innowise.task.dto.UserDto;
import com.innowise.task.entity.Role;
import com.innowise.task.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserDto toDto(User user) {
    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setUsername(user.getUsername());
    userDto.setEmail(user.getEmail());
    Role role = user.getRole();
    String roleName = role.getName();
    userDto.setRole(roleName);
    return userDto;
  }
}
package com.innowise.task.config;

import com.innowise.task.entity.Role;
import com.innowise.task.exception.InitializationFailedException;
import com.innowise.task.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataBaseInitializer implements CommandLineRunner {
  private final RoleRepository roleRepository;

  public DataBaseInitializer(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    try {
      if (roleRepository.findByName("ROLE_USER").isEmpty()) { //todo constant
        Role role = new Role();
        role.setName("ROLE_USER"); //todo constant
        roleRepository.save(role);
      }
      if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) { //todo constant
        Role role = new Role();
        role.setName("ROLE_ADMIN"); //todo constant
        roleRepository.save(role);
      }
    } catch (Exception e) {
      throw new InitializationFailedException("Failed to initialize database");
    }
  }
}

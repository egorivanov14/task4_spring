package com.innowise.task.security;

import com.innowise.task.security.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            // 1. ОТКЛЮЧАЕМ CSRF.
            // Это защита от подделки межсайтовых запросов, которая нужна только
            // если мы используем сессии (JSESSIONID) и Cookies. Для JWT (где токен в заголовке) это излишне и будет блокировать POST-запросы.
            .csrf(AbstractHttpConfigurer::disable)

            // 2. ДЕЛАЕМ API ПОЛНОСТЬЮ STATELESS (без состояния).
            // Мы говорим Spring: "Не создавай HttpSession и не сохраняй SecurityContext в сессии".
            // При каждом запросе контекст будет собираться заново нашим JwtFilter.
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 3. НАСТРАИВАЕМ ПРАВИЛА ДОСТУПА К ЭНДПОИНТАМ
            .authorizeHttpRequests(auth -> auth
                    // Разрешаем всем (даже без токена) доступ к регистрации и логину
                    .requestMatchers("/api/users/register", "/api/users/login").permitAll()

                    // (Опционально) Разрешаем доступ к Swagger/OpenAPI, если будешь использовать
                    // .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                    // ВСЕ остальные запросы требуют аутентификации (наличия валидного токена)
                    .anyRequest().authenticated()
            )

            // 4. ДОБАВЛЯЕМ НАШ ФИЛЬТР В ЦЕПОЧКУ
            // Мы встраиваем его ПЕРЕД стандартным фильтром аутентификации по логину/паролю.
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
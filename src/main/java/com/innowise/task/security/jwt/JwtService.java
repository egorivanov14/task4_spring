package com.innowise.task.security.jwt;

import com.innowise.task.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;

@Service
public class JwtService {
  private final String secretKey;
  private final Long expiration;

  public JwtService(@Value("${jwt.secret-key}") String secretKey, @Value("${jwt.expiration}") Long expiration) {
    this.secretKey = secretKey;
    this.expiration = expiration;
  }

  public String generateJwtToken(UserDetails userDetails) {
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    String roleName = authorities.iterator().next().getAuthority();
    String username = userDetails.getUsername();
    long now = System.currentTimeMillis();
    long exp = now + expiration;
    SecretKey key = getSignInKey();
    return Jwts.builder()
            .claim("role", roleName) //todo compact constants
            .subject(username)
            .issuedAt(new Date(now))
            .expiration(new Date(exp))
            .signWith(key)
            .compact();
  }

  public Claims validateAndGetClaims(String token) {
    SecretKey key = getSignInKey();
    try {
      return Jwts.parser()
              .verifyWith(key)
              .build()
              .parseSignedClaims(token)
              .getPayload();
    } catch (JwtException e) {
      throw new UnauthorizedException(e.getMessage());
    }
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return new SecretKeySpec(keyBytes, "HmacSHA256");
  }
}
package com.starwars.challenge.conexa.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtServiceTest {

    private JwtService jwtService;

    private static final String TEST_SECRET = "GQIapajY+tfPeXP93bmKi7wG2Os6E991nwp5mqK3kAk=";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L); // 1 hora
    }

    @Test
    void generateToken_shouldCreateTokenThatContainsTheUsername() {
        String token = jwtService.generateToken("Matias");

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("Matias");
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenUsernameMatchesAndTokenNotExpired() {
        String token = jwtService.generateToken("Matias");
        boolean isValid = jwtService.isTokenValid(token, "Matias");
        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenUsernameDoesNotMatch() {
        String token = jwtService.generateToken("Matias");
        boolean isValid = jwtService.isTokenValid(token, "otherUser");
        assertThat(isValid).isFalse();
    }

    @Test
    void extractUsername_shouldThrowException_whenTokenIsExpired() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String expiredToken = jwtService.generateToken("Matias");

        assertThatThrownBy(() -> jwtService.extractUsername(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }
}

package dev.esteban.mssecurity.service;

import dev.esteban.mssecurity.dto.UserLogDto;
import dev.esteban.mssecurity.model.User;
import dev.esteban.mssecurity.repository.UserRepository;
import dev.esteban.mssecurity.util.RoleUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    private JWTService jwtService;

    private User testUser;
    private UserLogDto userLogDto;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService(jwtEncoder);
        ReflectionTestUtils.setField(jwtService, "userRepository", userRepository);
        ReflectionTestUtils.setField(jwtService, "passwordEncoder", passwordEncoder);

        testUser = new User("John", "Doe", "john@example.com", "hashedPassword", RoleUser.CLIENT, LocalDate.of(1990, 1, 1));
        testUser.setId(1L);

        userLogDto = new UserLogDto();
        userLogDto.setEmail("john@example.com");
        userLogDto.setPassword("password123");
    }

    @Test
    void createJwtToken_ShouldReturnToken_WhenValidCredentials() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mock.jwt.token");
        when(jwtEncoder.encode(any())).thenReturn(mockJwt);

        // Act
        String token = jwtService.createJwtToken(userLogDto);

        // Assert
        assertNotNull(token);
        assertEquals("mock.jwt.token", token);
        verify(userRepository, times(2)).findByEmail("john@example.com");
        verify(passwordEncoder).matches("password123", "hashedPassword");
    }

    @Test
    void createJwtToken_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jwtService.createJwtToken(userLogDto));
        assertEquals("Email not found", exception.getMessage());
    }

    @Test
    void createJwtToken_ShouldThrowException_WhenInvalidPassword() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jwtService.createJwtToken(userLogDto));
        assertEquals("Password is incorrect", exception.getMessage());
    }
}
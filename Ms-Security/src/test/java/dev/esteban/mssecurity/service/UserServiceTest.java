package dev.esteban.mssecurity.service;

import dev.esteban.mssecurity.dto.UserLogDto;
import dev.esteban.mssecurity.dto.UserRegisterDTO;
import dev.esteban.mssecurity.model.User;
import dev.esteban.mssecurity.repository.UserRepository;
import dev.esteban.mssecurity.util.RoleUser;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserLogDto userLogDto;
    private UserRegisterDTO userRegisterDTO;

    @BeforeEach
    void setUp() {
        testUser = new User("John", "Doe", "john@example.com", "hashedPassword", RoleUser.CLIENT, LocalDate.of(1990, 1, 1));
        testUser.setId(1L);

        userLogDto = new UserLogDto();
        userLogDto.setEmail("john@example.com");
        userLogDto.setPassword("password123");

        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setFirstName("Jane");
        userRegisterDTO.setLastName("Smith");
        userRegisterDTO.setEmail("jane@example.com");
        userRegisterDTO.setPassword("password123");
        userRegisterDTO.setBirthDate(LocalDate.of(1995, 5, 15));
    }

    @Test
    void loginUser_ShouldReturnOk_WhenValidCredentials() {
        // Arrange
        when(jwtService.createJwtToken(userLogDto)).thenReturn("mock.jwt.token");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<?> result = userService.loginUser(userLogDto, response);

        // Assert
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(response).addCookie(any());
        verify(jwtService).createJwtToken(userLogDto);
    }

    @Test
    void loginUser_ShouldReturnUnauthorized_WhenInvalidCredentials() {
        // Arrange
        when(jwtService.createJwtToken(userLogDto)).thenThrow(new RuntimeException("Password is incorrect"));

        // Act
        ResponseEntity<?> result = userService.loginUser(userLogDto, response);

        // Assert
        assertEquals(401, result.getStatusCode().value());
    }

    @Test
    void registerUser_ShouldReturnOk_WhenValidData() {
        // Arrange
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        // Act
        ResponseEntity<?> result = userService.registerUser(userRegisterDTO);

        // Assert
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        // Act
        ResponseEntity<?> result = userService.registerUser(userRegisterDTO);

        // Assert
        assertEquals(400, result.getStatusCode().value());
        assertEquals("Email already exists", result.getBody());
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenPasswordTooShort() {
        // Arrange
        userRegisterDTO.setPassword("short");
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);

        // Act
        ResponseEntity<?> result = userService.registerUser(userRegisterDTO);

        // Assert
        assertEquals(400, result.getStatusCode().value());
        assertEquals("Password must be at least 8 characters long", result.getBody());
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenUserTooYoung() {
        // Arrange
        userRegisterDTO.setBirthDate(LocalDate.now().minusYears(17));
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);

        // Act
        ResponseEntity<?> result = userService.registerUser(userRegisterDTO);

        // Assert
        assertEquals(400, result.getStatusCode().value());
        assertEquals("User must be at least 18 years old", result.getBody());
    }
}
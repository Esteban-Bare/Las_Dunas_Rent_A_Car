package dev.esteban.mssecurity.controller;

import dev.esteban.mssecurity.model.User;
import dev.esteban.mssecurity.repository.UserRepository;
import dev.esteban.mssecurity.util.RoleUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtValidationControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private JwtValidationController jwtValidationController;

    private User adminUser;
    private User managerUser;
    private User clientUser;

    @BeforeEach
    void setUp() {
        adminUser = new User("Admin", "User", "admin@example.com", "password", RoleUser.ADMIN, LocalDate.of(1980, 1, 1));
        managerUser = new User("Manager", "User", "manager@example.com", "password", RoleUser.MANAGER, LocalDate.of(1985, 1, 1));
        clientUser = new User("Client", "User", "client@example.com", "password", RoleUser.CLIENT, LocalDate.of(1990, 1, 1));
    }

    @Test
    void validateToken_ShouldReturnValid_WhenTokenIsValid() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user@example.com");
        when(jwt.getClaim("role")).thenReturn("CLIENT");
        when(jwt.getClaim("userId")).thenReturn("1");
        when(jwtDecoder.decode("validtoken")).thenReturn(jwt);

        // Act
        ResponseEntity<?> result = jwtValidationController.validateToken("Bearer validtoken");

        // Assert
        assertTrue(result.getStatusCode().is2xxSuccessful());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("valid"));
        assertEquals("user@example.com", body.get("subject"));
    }

    @Test
    void validateToken_ShouldReturnInvalid_WhenTokenIsInvalid() {
        // Arrange
        when(jwtDecoder.decode("invalidtoken")).thenThrow(new RuntimeException("Invalid token"));

        // Act
        ResponseEntity<?> result = jwtValidationController.validateToken("Bearer invalidtoken");

        // Assert
        assertTrue(result.getStatusCode().is2xxSuccessful());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("valid"));
    }

    @Test
    void canAccess_ShouldReturnTrue_WhenManagerAccessesManagerBackoffice() {
        // Arrange
        when(userRepository.findByEmail("manager@example.com")).thenReturn(Optional.of(managerUser));

        // Act
        ResponseEntity<?> result = jwtValidationController.canAccess("manager@example.com", "manager-backoffice");

        // Assert
        assertTrue(result.getStatusCode().is2xxSuccessful());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("authorized"));
    }

    @Test
    void canAccess_ShouldReturnTrue_WhenAdminAccessesAnyResource() {
        // Arrange
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // Act
        ResponseEntity<?> result1 = jwtValidationController.canAccess("admin@example.com", "manager-backoffice");
        ResponseEntity<?> result2 = jwtValidationController.canAccess("admin@example.com", "admin-backoffice");

        // Assert
        assertTrue(result1.getStatusCode().is2xxSuccessful());
        assertTrue(result2.getStatusCode().is2xxSuccessful());
        Map<String, Object> body1 = (Map<String, Object>) result1.getBody();
        Map<String, Object> body2 = (Map<String, Object>) result2.getBody();
        assertTrue((Boolean) body1.get("authorized"));
        assertTrue((Boolean) body2.get("authorized"));
    }

    @Test
    void canAccess_ShouldReturnFalse_WhenClientAccessesBackoffice() {
        // Arrange
        when(userRepository.findByEmail("client@example.com")).thenReturn(Optional.of(clientUser));

        // Act
        ResponseEntity<?> result = jwtValidationController.canAccess("client@example.com", "manager-backoffice");

        // Assert
        assertTrue(result.getStatusCode().is2xxSuccessful());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("authorized"));
    }

    @Test
    void canAccess_ShouldReturnFalse_WhenManagerAccessesAdminBackoffice() {
        // Arrange
        when(userRepository.findByEmail("manager@example.com")).thenReturn(Optional.of(managerUser));

        // Act
        ResponseEntity<?> result = jwtValidationController.canAccess("manager@example.com", "admin-backoffice");

        // Assert
        assertTrue(result.getStatusCode().is2xxSuccessful());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("authorized"));
    }
}
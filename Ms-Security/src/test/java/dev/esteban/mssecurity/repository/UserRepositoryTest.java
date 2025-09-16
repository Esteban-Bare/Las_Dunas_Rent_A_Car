package dev.esteban.mssecurity.repository;

import dev.esteban.mssecurity.model.User;
import dev.esteban.mssecurity.util.RoleUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        User user = new User("John", "Doe", "john@example.com", "password", RoleUser.CLIENT, LocalDate.of(1990, 1, 1));
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> found = userRepository.findByEmail("john@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
        assertEquals("john@example.com", found.get().getEmail());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        // Act
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Arrange
        User user = new User("John", "Doe", "john@example.com", "password", RoleUser.CLIENT, LocalDate.of(1990, 1, 1));
        entityManager.persistAndFlush(user);

        // Act
        boolean exists = userRepository.existsByEmail("john@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // Act
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
    }
}
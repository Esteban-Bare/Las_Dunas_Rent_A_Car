package dev.esteban.mssecurity.service;

import dev.esteban.mssecurity.dto.UserRegisterDTO;
import dev.esteban.mssecurity.model.User;
import dev.esteban.mssecurity.repository.UserRepository;
import dev.esteban.mssecurity.util.RoleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> registerUser(UserRegisterDTO userRegisterDTO) {
        if (userRepository.existsByEmail((userRegisterDTO.getEmail()))) {
            return ResponseEntity.status(400).body("Email already exists");
        }
        if (userRegisterDTO.getFirstName() == null || userRegisterDTO.getLastName() == null || userRegisterDTO.getEmail() == null || userRegisterDTO.getPassword() == null) {
            return ResponseEntity.status(400).body("All fields are required");
        }
        if (userRegisterDTO.getPassword().length() < 8) {
            return ResponseEntity.status(400).body("Password must be at least 8 characters long");
        }
        if (userRegisterDTO.getFirstName().length() < 2 || userRegisterDTO.getLastName().length() < 2) {
            return ResponseEntity.status(400).body("First name and last name must be at least 2 characters long");
        }
        if (userRegisterDTO.getBirthDate() == null) {
            return ResponseEntity.status(400).body("Birth date is required");
        }
        if (userRegisterDTO.getBirthDate().isAfter(java.time.LocalDate.now())) {
            return ResponseEntity.status(400).body("Birth date cannot be in the future");
        }
        if (userRegisterDTO.getBirthDate().isAfter(java.time.LocalDate.now().minusYears(18))) {
            return ResponseEntity.status(400).body("User must be at least 18 years old");
        }
        if (userRegisterDTO.getEmail().length() < 5 || !userRegisterDTO.getEmail().contains("@")) {
            return ResponseEntity.status(400).body("Invalid email format");
        }
        // Save the user to the database
        User user = new User();
        user.setFirstName(userRegisterDTO.getFirstName());
        user.setLastName(userRegisterDTO.getLastName());
        user.setBirthDate(userRegisterDTO.getBirthDate());
        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setRole(RoleUser.CLIENT);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
}

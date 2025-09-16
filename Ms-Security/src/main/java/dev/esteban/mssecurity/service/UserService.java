package dev.esteban.mssecurity.service;

import dev.esteban.mssecurity.dto.UserLogDto;
import dev.esteban.mssecurity.dto.UserRegisterDTO;
import dev.esteban.mssecurity.model.User;
import dev.esteban.mssecurity.repository.UserRepository;
import dev.esteban.mssecurity.util.RoleUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> loginUser(UserLogDto info, HttpServletResponse response) {
        try {
            String token = jwtService.createJwtToken(info);

            User user = userRepository.findByEmail(info.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

            Cookie cookie = new Cookie("JWT", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400);

            response.addCookie(cookie);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("email", user.getEmail());
            userInfo.put("role", user.getRole());
            userInfo.put("userId", user.getId());

            return ResponseEntity.ok(userInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Problem during login"));
        }
    }

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

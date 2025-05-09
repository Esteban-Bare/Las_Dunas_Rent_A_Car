package dev.esteban.mssecurity.controller;

import dev.esteban.mssecurity.dto.JwtJson;
import dev.esteban.mssecurity.dto.UserLogDto;
import dev.esteban.mssecurity.dto.UserRegisterDTO;
import dev.esteban.mssecurity.model.User;
import dev.esteban.mssecurity.repository.UserRepository;
import dev.esteban.mssecurity.service.JWTService;
import dev.esteban.mssecurity.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogDto info, HttpServletResponse response) {
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

            return ResponseEntity.ok(userInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Problem during login"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
        System.out.println("Logout successful");
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @PostMapping("/check-header-bearer")
    public ResponseEntity<?> checkHeaderBearer(@RequestHeader("Authorization") String token) {
        // I need to see if the header bearer token is present and return it in the response
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "No token provided"));
        }
        // I dont need to decode the token, just return it
        String jwt = token.replace("Bearer ", "");
        return ResponseEntity.ok(Map.of("token", jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO user) {
        ResponseEntity<?> response = userService.registerUser(user);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }
}

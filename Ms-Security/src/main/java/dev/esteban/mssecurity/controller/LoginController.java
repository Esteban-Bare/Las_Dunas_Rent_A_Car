package dev.esteban.mssecurity.controller;

import dev.esteban.mssecurity.dto.UserLogDto;
import dev.esteban.mssecurity.dto.UserRegisterDTO;
import dev.esteban.mssecurity.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogDto info, HttpServletResponse response) {
        ResponseEntity<?> responseEntity = userService.loginUser(info, response);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(responseEntity.getBody());
        } else {
            System.out.println(responseEntity.getBody());
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
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
            System.out.println(response.getBody());
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }
}

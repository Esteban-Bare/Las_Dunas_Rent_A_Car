package dev.esteban.mssecurity.controller;

import dev.esteban.mssecurity.model.User;
import dev.esteban.mssecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class JwtValidationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtDecoder jwtDecoder;

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        System.out.println("Validating token: " + token);
        try {
            System.out.println("Decoding token...");
            String jwt = token.replace("Bearer ", "");
            Jwt decodedJwt = jwtDecoder.decode(jwt);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("subject", decodedJwt.getSubject());
            response.put("role", decodedJwt.getClaim("role"));
            response.put("userId", decodedJwt.getClaim("userId"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/can-access/{resource}")
    public ResponseEntity<?> canAccess(@RequestHeader("X-User-Email") String email, @PathVariable String resource) {
        System.out.println("Checking access for path: " + resource);
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            boolean isAuthorized = switch (resource) {
                case "manager-backoffice" ->
                        user.getRole().toString().equals("MANAGER") || user.getRole().toString().equals("ADMIN");
                case "admin-backoffice" ->
                        user.getRole().toString().equals("ADMIN");
                default -> false;
            };

            return ResponseEntity.ok(Map.of("authorized", isAuthorized));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("authorized", false, "error", e.getMessage()));
        }
    }
}

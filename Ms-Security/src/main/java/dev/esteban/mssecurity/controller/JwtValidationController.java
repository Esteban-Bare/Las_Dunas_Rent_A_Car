package dev.esteban.mssecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class JwtValidationController {

    @Autowired
    private JwtDecoder jwtDecoder;

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        System.out.println("Validating token: " + token);
        try {
            String jwt = token.replace("Bearer ", "");
            Jwt decodedJwt = jwtDecoder.decode(jwt);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("subject", decodedJwt.getSubject());
            response.put("role", decodedJwt.getClaim("role"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false, "error", e.getMessage()));
        }
    }
}

package dev.esteban.mssecurity.service;

import dev.esteban.mssecurity.dto.UserLogDto;
import dev.esteban.mssecurity.model.User;
import dev.esteban.mssecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class JWTService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private JwtEncoder jwtEncoder;

    public JWTService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }
    public String createJwtToken(UserLogDto info) {
        if (!isValidUser(info)) {
            throw new RuntimeException("Invalid credentials");
        }
        User user = userRepository.findByEmail(info.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1,ChronoUnit.DAYS))
                .subject(info.getEmail())
                .claim("role", user.getRole())
                .build();
        JwtEncoderParameters params = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.jwtEncoder.encode(params).getTokenValue();
    }

    private boolean isValidUser(UserLogDto info) {
        return userRepository.findByEmail(info.getEmail())
                .filter(user -> passwordEncoder.matches(info.getPassword(), user.getPassword()))
                .isPresent();
    }
}

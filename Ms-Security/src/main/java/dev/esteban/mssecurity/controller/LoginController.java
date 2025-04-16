package dev.esteban.mssecurity.controller;

import dev.esteban.mssecurity.dto.UserLogDto;
import dev.esteban.mssecurity.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private JWTService jwtService;

    @PostMapping("/login")
    public String login(@RequestBody UserLogDto info) {
        String token = jwtService.createJwtToken(info);
        return token;
    }
}

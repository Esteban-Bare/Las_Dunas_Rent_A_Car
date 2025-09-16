package dev.esteban.mssecurity.controller;

import dev.esteban.mssecurity.dto.UserClientDto;
import dev.esteban.mssecurity.dto.UserUpdateDto;
import dev.esteban.mssecurity.model.User;
import dev.esteban.mssecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/update/role")
    public ResponseEntity<?> updateRole(@RequestBody UserUpdateDto userUpdateDto, @RequestHeader("X-User-Role") String role) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        Optional<User> user = userRepository.findById(Long.valueOf(userUpdateDto.getId()));
        if (user.isPresent()) {
            user.get().setRole(userUpdateDto.getRole());
            userRepository.save(user.get());
            return ResponseEntity.ok("User role updated successfully");
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(403).body("Access denied");
        }
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/client")
    public ResponseEntity<?> getClientById(@RequestHeader("X-User-Id") String id) {
        System.out.println(id);
        User user = userRepository.findById(Long.valueOf(id)).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        return ResponseEntity.ok(new UserClientDto(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCreatedAt()
        ));
    }

    @GetMapping("/count")
    public ResponseEntity<?> getUserCount(@RequestHeader("X-User-Role") String role) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(403).body("Access denied");
        }
        long count = userRepository.count();
        return ResponseEntity.ok(count);
    }
}

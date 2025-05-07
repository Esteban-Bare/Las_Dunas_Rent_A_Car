package dev.esteban.mssecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDate;
}

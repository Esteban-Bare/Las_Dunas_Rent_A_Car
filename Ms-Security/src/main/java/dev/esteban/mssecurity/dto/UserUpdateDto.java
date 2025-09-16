package dev.esteban.mssecurity.dto;

import dev.esteban.mssecurity.util.RoleUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    private String id;
    private RoleUser role;
}

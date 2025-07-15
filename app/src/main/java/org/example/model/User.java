package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "JwtUsersTable")
public class User {
    @Id
    private String username;
    private String password;
    private String role;
}

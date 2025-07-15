package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Represents a user entity in the JWT authentication system.
 * This class serves as the primary user model for authentication and authorization,
 * storing essential user credentials and role information.
 * 
 * <p>The user entity is mapped to the "JwtUsersTable" database table and uses
 * the username as the primary key. Each user has a password (stored encrypted)
 * and a role that determines their access permissions within the application.</p>
 * 
 * @author Spring Security with JWT Application
 * @since 1.0
 */
@Entity
@Data
@Table(name = "JwtUsersTable")
public class User {
    
    /**
     * The unique username that serves as the primary identifier for the user.
     * This field is used as the primary key in the database and must be unique
     * across all users in the system.
     */
    @Id
    private String username;
    
    /**
     * The user's password, stored in encrypted format using BCrypt.
     * This field should never be stored or transmitted in plain text.
     * The password is used for authentication during the login process.
     */
    private String password;
    
    /**
     * The role assigned to the user, determining their access permissions.
     * Common roles include "USER", "ADMIN", etc. This field is used by
     * Spring Security for authorization decisions throughout the application.
     */
    private String role;
}

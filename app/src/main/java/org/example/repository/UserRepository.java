package org.example.repository;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User entities in the database.
 * This interface extends JpaRepository to provide standard CRUD operations
 * and custom query methods for User entities.
 * 
 * <p>The repository handles database operations for user management including
 * user creation, retrieval, updates, and deletion. It uses Spring Data JPA
 * to automatically implement the repository methods.</p>
 * 
 * @author Spring Security with JWT Application
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * Finds a user by their username.
     * This method is primarily used during authentication to retrieve user
     * credentials and role information from the database.
     * 
     * @param username the unique username to search for, must not be null
     * @return an Optional containing the user if found, or empty if no user
     *         exists with the specified username
     */
    Optional<User> findByUsername(String username);
}

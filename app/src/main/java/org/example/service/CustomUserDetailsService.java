package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom implementation of Spring Security's UserDetailsService interface.
 * This service is responsible for loading user-specific data during authentication
 * and providing user details to the Spring Security framework.
 * 
 * <p>The service integrates with the application's User repository to retrieve
 * user information from the database and converts it into Spring Security's
 * UserDetails format. This bridge between the application's user model and
 * Spring Security's authentication system is essential for JWT-based authentication.</p>
 * 
 * <p>The service loads user information including username, password, and authorities
 * (roles) that are used throughout the authentication and authorization process.</p>
 * 
 * @author Spring Security with JWT Application
 * @since 1.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repository for accessing user data from the database.
     * Used to retrieve user information during authentication.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Loads user details by username for Spring Security authentication.
     * This method is called by Spring Security during authentication to retrieve
     * user information including credentials and authorities.
     * 
     * <p>The method retrieves the user from the database using the provided username,
     * and converts the application's User entity into Spring Security's UserDetails
     * format. The user's role is converted into a GrantedAuthority for authorization
     * purposes.</p>
     * 
     * @param username the username identifying the user whose data is required,
     *                 must not be null
     * @return a fully populated UserDetails object containing user information
     *         including username, password, and authorities
     * @throws UsernameNotFoundException if the user could not be found in the database
     *                                   or the username is null
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}

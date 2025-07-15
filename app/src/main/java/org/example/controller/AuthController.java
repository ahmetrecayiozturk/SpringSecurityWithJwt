package org.example.controller;

import org.checkerframework.checker.units.qual.A;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.example.dto.AuthRequest;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for handling authentication operations.
 * This controller provides endpoints for user registration, login, and basic
 * authentication testing in a JWT-based security system.
 * 
 * <p>The controller handles the following operations:</p>
 * <ul>
 *   <li>User registration with automatic password encryption</li>
 *   <li>User authentication with JWT token generation</li>
 *   <li>Authentication testing for protected endpoints</li>
 * </ul>
 * 
 * <p>All endpoints follow RESTful conventions and return appropriate HTTP status
 * codes along with meaningful response messages. The controller integrates with
 * Spring Security's authentication manager and custom JWT utilities to provide
 * a complete authentication solution.</p>
 * 
 * @author Spring Security with JWT Application
 * @since 1.0
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authManager;

    private UserRepository userRepository;

    private JwtUtil jwtUtil;

    private PasswordEncoder passwordEncoder;

    /**
     * Constructs an AuthController with required dependencies.
     * This constructor initializes the controller with all necessary components
     * for handling authentication operations.
     * 
     * @param authManager the authentication manager for processing login attempts
     * @param userRepository the repository for user data operations
     * @param jwtUtil the utility for JWT token operations
     * @param passwordEncoder the encoder for password hashing and verification
     */
    public AuthController(AuthenticationManager authManager,
                          UserRepository userRepository,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user in the system.
     * This endpoint allows new users to create accounts by providing a username
     * and password. The password is automatically encrypted using BCrypt before
     * being stored in the database.
     * 
     * <p>The registration process:</p>
     * <ul>
     *   <li>Checks if the username already exists</li>
     *   <li>Encrypts the provided password using BCrypt</li>
     *   <li>Assigns a default "USER" role to the new user</li>
     *   <li>Saves the user to the database</li>
     * </ul>
     * 
     * @param authRequest the authentication request containing username and password,
     *                    must not be null
     * @return ResponseEntity with success message if registration is successful,
     *         or bad request with error message if username already exists
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest authRequest){
        if(userRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        else{
            User newUser = new User();
            newUser.setUsername(authRequest.getUsername());
            //şifreyi encode ederek veriyoruz
            newUser.setPassword(passwordEncoder.encode(authRequest.getPassword()));
            //varsayılan rol olarak User dedim ama bunu sonradan değiştirebiliriz eğer requestte verseydik falan orda haleldebilirdik mesela
            //alıcı ve satıcı kayıt olurken bunu kayıt sırasında belirtince burada biz onu rol olarak verebiliriz, bu default hali
            newUser.setRole("USER");
            userRepository.save(newUser);
            return ResponseEntity.ok("User registered successfully");
        }
    }
    
    /**
     * Authenticates a user and generates a JWT token.
     * This endpoint validates user credentials and returns a JWT token that can
     * be used for accessing protected endpoints throughout the application.
     * 
     * <p>The login process:</p>
     * <ul>
     *   <li>Authenticates the user using Spring Security's authentication manager</li>
     *   <li>Generates a JWT token if authentication is successful</li>
     *   <li>Returns the token in the response for client-side storage</li>
     * </ul>
     * 
     * @param authRequest the authentication request containing username and password,
     *                    must not be null
     * @return ResponseEntity with JWT token if authentication is successful,
     *         or unauthorized status with error message if credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest){
            //burada authenticationManager'ı kullanarak kullanıcıyı authenticate ediyoruz, bunu yaparken UsernamePasswordAuthenticationToken kullanıyoruz
            //bu token, kullanıcı adı ve şifre ile oluşturuluyor ve authenticationManager tarafından doğrulanıyor.
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), authRequest.getPassword()));

            if(auth.isAuthenticated()){
                //eğer kullanıcı doğru ise yani authentication başarılıysa jwt token oluşturuyoruz
                String token = jwtUtil.generateToken(authRequest.getUsername());
                return ResponseEntity.ok(Map.of("token", token).toString());
            } else {
                //eğer kullanıcı doğrulanmadıysa, hata mesajı döndürüyoruz
                return ResponseEntity.status(401).body("Invalid username or password");
            }
    }
    
    /**
     * Tests authentication by verifying JWT token validity.
     * This endpoint serves as a simple test mechanism to verify that JWT
     * authentication is working properly. It requires a valid JWT token
     * in the Authorization header to access.
     * 
     * <p>This endpoint is useful for:</p>
     * <ul>
     *   <li>Testing JWT token validity after login</li>
     *   <li>Verifying that authentication filters are working correctly</li>
     *   <li>Debugging authentication issues during development</li>
     * </ul>
     * 
     * @return ResponseEntity with success message if the user is authenticated,
     *         or unauthorized status if the JWT token is invalid or missing
     */
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok(Map.of("message", "kullanıcı doğrulandı").toString());
    }
}

package org.example.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Utility class for handling JSON Web Token (JWT) operations.
 * This class provides methods for generating, parsing, and validating JWT tokens
 * used in the authentication and authorization process.
 * 
 * <p>The utility handles token generation with user-specific claims, token validation
 * including expiration checks, and extraction of user information from tokens.
 * It uses HMAC SHA-256 algorithm for signing tokens and includes configurable
 * expiration times for enhanced security.</p>
 * 
 * <p><strong>Security Note:</strong> The secret key should be stored securely
 * using environment variables or external configuration in production environments.</p>
 * 
 * @author Spring Security with JWT Application
 * @since 1.0
 */
@Component
public class JwtUtil {
    
    /**
     * Secret key used for signing JWT tokens.
     * <p><strong>Important:</strong> This key should be stored securely using
     * environment variables or external configuration in production environments.</p>
     */
    private String mySecretKey = "thisismysecretkeyanditshouldbeverylonganditshouldnotbehardcoded"; // Bu keyi daha güvenli bir şekilde saklamalısınız, örneğin environment variable olarak
    
    /**
     * Token expiration time in milliseconds (1 hour).
     * This determines how long a JWT token remains valid after generation.
     */
    private long myExpirationTime = 3600000; // 1 hour in milliseconds

    /**
     * Generates a JWT token for the specified username.
     * The token includes the username as the subject, issued time, expiration time,
     * and is signed using HMAC SHA-256 algorithm with the secret key.
     * 
     * @param username the username to be included as the token subject, must not be null
     * @return a signed JWT token string that can be used for authentication
     */
    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)//burada subject olarak usernameyi veriyoruz
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + myExpirationTime)) // 30 dk
                .signWith(SignatureAlgorithm.HS256, mySecretKey)
                .compact();

    }
    
    /**
     * Extracts the username from a JWT token.
     * Parses the token using the secret key and retrieves the subject claim,
     * which contains the username.
     * 
     * @param token the JWT token to parse, must not be null
     * @return the username extracted from the token's subject claim
     * @throws io.jsonwebtoken.JwtException if the token is invalid or cannot be parsed
     */
    public String extractUsername(String token){
        return Jwts.parser()
                .setSigningKey(mySecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();//burada subject olarak atadığımız usernameyi çağırıyoruz
    }
    
    /**
     * Checks if a JWT token has expired.
     * Compares the token's expiration date with the current system time
     * to determine if the token is still valid.
     * 
     * @param token the JWT token to check, must not be null
     * @return true if the token has expired, false if it's still valid
     * @throws io.jsonwebtoken.JwtException if the token is invalid or cannot be parsed
     */
    public Boolean isTokenExpired(String token){
        return Jwts.parser().setSigningKey(mySecretKey).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
    
    /**
     * Validates a JWT token against user details.
     * Performs comprehensive validation including username matching and expiration checking.
     * The token is considered valid if the username in the token matches the provided
     * user details and the token has not expired.
     * 
     * @param token the JWT token to validate, must not be null
     * @param userDetails the user details to validate against, must not be null
     * @return true if the token is valid and matches the user details, false otherwise
     * @throws io.jsonwebtoken.JwtException if the token is invalid or cannot be parsed
     */
    public Boolean validateToken(String token, UserDetails userDetails){
        //tokenin username ile eşleşip eşleşmediğini kontrol ediyoruz
        String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

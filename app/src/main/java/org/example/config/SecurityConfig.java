package org.example.config;
import org.example.repository.UserRepository;
import org.example.security.JwtFilter;
import org.example.security.JwtUtil;
import org.example.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.swing.*;

/**
 * Spring Security configuration class for JWT-based authentication.
 * This configuration class sets up the security framework for the application,
 * defining authentication mechanisms, authorization rules, and JWT-specific filters.
 * 
 * <p>The configuration implements a stateless security model using JWT tokens,
 * eliminating the need for server-side sessions. It configures:</p>
 * <ul>
 *   <li>HTTP security with CSRF protection disabled for stateless operation</li>
 *   <li>Public endpoints for user registration and login</li>
 *   <li>JWT filter for token validation on protected endpoints</li>
 *   <li>BCrypt password encoding for secure password storage</li>
 *   <li>Custom user details service for user authentication</li>
 * </ul>
 * 
 * <p>This configuration ensures that all API endpoints except login and registration
 * require valid JWT tokens for access, providing a secure authentication mechanism
 * for the application.</p>
 * 
 * @author Spring Security with JWT Application
 * @since 1.0
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Configures the security filter chain for HTTP requests.
     * This method sets up the main security configuration including URL authorization,
     * session management, and filter chain setup for JWT authentication.
     * 
     * <p>The configuration:</p>
     * <ul>
     *   <li>Disables CSRF protection as it's not needed for stateless APIs</li>
     *   <li>Permits all access to login and registration endpoints</li>
     *   <li>Requires authentication for all other endpoints</li>
     *   <li>Configures stateless session management</li>
     *   <li>Adds the JWT filter before username/password authentication</li>
     * </ul>
     * 
     * @param http the HttpSecurity object to configure security settings
     * @return a configured SecurityFilterChain for the application
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF'yi devre dışı bırakmak için 'csrf().disable()' yerine 'csrf().disable().and()' ekliyoruz.
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        //bu equestmachers'in içinde permitAll dediğimiz endpointlere herkesin erişebileceği anlamına geliyor, ancak anyRequest().authenticated() dediğimizde ise public olmayan tüm
                        //endpointlere bir authentication işlemi uygulayacağımız anlamına geliyor.
                        .requestMatchers("/auth/login","/auth/register").permitAll()//herkesin erişebileceği bir endpoint
                        .anyRequest().authenticated()  //geri kalan tüm isteklerin kimlik doğrulaması gerektirmesini sağlıyoruz.
                )
                //session kullanmıcaz yani token sürekli olacak ve her requestte kontrol edeceğiz
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //userDetailsService'yi atayarak tokeni doğrulamak için kullanıyoruz
                .userDetailsService(userDetailsService)
                //buraya jwtFilter'ı ekliyoruz,bu filter her requestte çalışacak ve tokeni kontrol edecek, biz b urada username ve password ile giriş yapmadığımız için bu filteri ekliyoruz
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                //burada build ediyoruz
                .build();
    }

    /**
     * Creates and configures the authentication manager bean.
     * This manager is responsible for processing authentication requests and
     * integrating with the custom user details service and password encoder.
     * 
     * <p>The authentication manager is configured with:</p>
     * <ul>
     *   <li>Custom user details service for loading user information</li>
     *   <li>BCrypt password encoder for password verification</li>
     * </ul>
     * 
     * @param http the HttpSecurity object used to obtain the authentication manager builder
     * @return a configured AuthenticationManager for the application
     * @throws Exception if an error occurs during authentication manager creation
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        //önce AuthBuilder'i oluşturuyoruz
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        //sonra da userDetailsService ve passwordEncoder'ı ekliyoruz
        authBuilder.userDetailsService(userDetailsService)
                //.passwordEncoder(NoOpPasswordEncoder.getInstance());
                // pawwsordleri iifreleme için BCrypt kullanıyoruz bunu ayrı bir bean ile tanımladık zaten
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }
    
    /**
     * Creates a BCrypt password encoder bean for secure password hashing.
     * This encoder is used throughout the application for encoding passwords
     * during user registration and verifying passwords during authentication.
     * 
     * <p>BCrypt is a strong, adaptive hashing function designed specifically
     * for password hashing. It automatically handles salt generation and
     * provides resistance against rainbow table and brute force attacks.</p>
     * 
     * @return a BCryptPasswordEncoder instance for password operations
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

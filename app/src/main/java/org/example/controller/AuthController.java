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

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authManager;

    private UserRepository userRepository;

    private JwtUtil jwtUtil;

    private PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager,
                          UserRepository userRepository,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

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
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok(Map.of("message", "kullanıcı doğrulandı").toString());
    }
}

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

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

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
    //passwordEncoder beani tanımlıyoruz,bu beani kullanarak şifreleri bCrypt ile şifreleyeceğiz
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

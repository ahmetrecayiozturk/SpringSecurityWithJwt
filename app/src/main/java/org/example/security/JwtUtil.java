package org.example.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    //Secret key için bir tanımlama yapıyoruz
    private String mySecretKey = "thisismysecretkeyanditshouldbeverylonganditshouldnotbehardcoded"; // Bu keyi daha güvenli bir şekilde saklamalısınız, örneğin environment variable olarak
    //JWT'nin süresini belirliyoruz
    private long myExpirationTime = 3600000; // 1 hour in milliseconds

    //burada token'i jwts builder ile generate ediyoruz
    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)//burada subject olarak usernameyi veriyoruz
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + myExpirationTime)) // 30 dk
                .signWith(SignatureAlgorithm.HS256, mySecretKey)
                .compact();

    }
    //burada subjectten usernameyi alıyoruz
    public String extractUsername(String token){
        return Jwts.parser()
                .setSigningKey(mySecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();//burada subject olarak atadığımız usernameyi çağırıyoruz
    }
    //burada tokenin süresinin dolup dolmadığını kontrol ediyoruz
    public Boolean isTokenExpired(String token){
        return Jwts.parser().setSigningKey(mySecretKey).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
    //burada tokenin geçerli olup olmadığını ve süresinin dolup dolmadığını kontrol ediyoruz
    public Boolean validateToken(String token, UserDetails userDetails){
        //tokenin username ile eşleşip eşleşmediğini kontrol ediyoruz
        String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

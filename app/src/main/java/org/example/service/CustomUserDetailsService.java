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

@Service
public class CustomUserDetailsService implements UserDetailsService {

    //we have injected userRepository çünkü burada userı bulmak için bunu kullanacağız
    @Autowired
    private UserRepository userRepository;

    //burada spring security'nini bize sağladığı userdetails'i kullanıyoruz buna uygun bir methodu var zaten ctrl+sol click ile gittiğimizde görüyoruz içini nasıl doldurmamız gerketiğini
    //yine spring security'dem userdetails'in sağladığı user nesnesine isim şifre ve rolleri veriyoruz ve bunu döndürüyoruz, tüm userdetail servicemiz basitçe budur
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

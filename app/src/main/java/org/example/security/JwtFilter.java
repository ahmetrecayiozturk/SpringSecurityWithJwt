package org.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //Login ve Register endpointlerini filtreleme, yani bu endpointlere gelen isteklerde token kontrolü yapmıyoruz
        String path = request.getServletPath();
        if (path.equals("/auth/login") || path.equals("/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }
        //önce null olarak token ve username değişkenlerini atıyoruz
        String token = null;
        String username = null;
        //1. önce request'in authorization headerini alıyoruz
        String authHeader = request.getHeader("Authorization");
        //2. eğer authorization headeri boş ise veya Bearer ile başlamıyorsa
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            //2.1 tokenin Bearer'i atılmış kısmı olan yer olduğunu bildiğimizden önce Bearer'i atıyoruz sonra tokeni alıyoruz
            token = authHeader.substring(7);
            //2.2 tokeni kullanarak username'yi alıyoruz
            username = jwtUtil.extractUsername(token);
        }
        //3. eğer username boş değilse ve authenticate olmamış ise user
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            //3.1 bir user details oluşturuyoruz ki bunu tokeni validate ederken kullanabilelim
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            //3.2 tokeni username ile valide ediyoruz ve eğer doğru ise bu bu tokeni securitycontextholder'daki token yapıyoruz
            if(jwtUtil.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken  = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //4. her bir requestte bu zincir devam etsin istiyoruz
        filterChain.doFilter(request, response);
    }

}

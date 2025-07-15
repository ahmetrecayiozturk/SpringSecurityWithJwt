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

/**
 * JWT authentication filter that processes incoming HTTP requests to validate JWT tokens.
 * This filter extends OncePerRequestFilter to ensure it runs exactly once per request
 * and handles JWT token extraction, validation, and security context setup.
 * 
 * <p>The filter operates by:</p>
 * <ul>
 *   <li>Extracting JWT tokens from the Authorization header</li>
 *   <li>Validating tokens using the JwtUtil service</li>
 *   <li>Setting up the Spring Security context for authenticated users</li>
 *   <li>Bypassing authentication for login and register endpoints</li>
 * </ul>
 * 
 * <p>This filter is essential for stateless JWT-based authentication, ensuring that
 * each request carries sufficient authentication information without relying on
 * server-side sessions.</p>
 * 
 * @author Spring Security with JWT Application
 * @since 1.0
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Processes each HTTP request to extract and validate JWT tokens.
     * This method implements the core JWT authentication logic, including token
     * extraction from headers, validation, and security context setup.
     * 
     * <p>The filter bypasses authentication for login and register endpoints,
     * allowing users to authenticate and create accounts without existing tokens.
     * For all other endpoints, it validates the JWT token and establishes the
     * security context if the token is valid.</p>
     * 
     * @param request the HTTP request containing potential JWT token in Authorization header
     * @param response the HTTP response object
     * @param filterChain the filter chain to continue request processing
     * @throws ServletException if servlet-related errors occur during processing
     * @throws IOException if I/O errors occur during request/response handling
     */
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

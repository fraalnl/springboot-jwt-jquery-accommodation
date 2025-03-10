package edu.tus.security;

import edu.tus.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

// processes incoming HTTP requests and validates JWT tokens. 
// It extends OncePerRequestFilter, ensuring that the filter executes only once per request
// even if the request is processed by multiple filters
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtUserDetailsService jwtUserDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, JwtUserDetailsService jwtUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @Override
    // processes every HTTP request before it reaches the controller
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); //Extracts the token by removing the "Bearer " prefix
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
                
                String role = jwtUtil.getRoleFromToken(token);
                
                // Create the authentication token with the proper authority
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, //No credentials are needed since authentication is based on the JWT
                                //Authorities (Roles): Wraps the extracted role in SimpleGrantedAuthority
                                Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(role))
                        );
                //Associates the authenticated user with the Spring Security Context
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } //Passes the request down the filter chain so it reaches the intended controller
        filterChain.doFilter(request, response);
    }
}

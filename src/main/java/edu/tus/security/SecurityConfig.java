package edu.tus.security;

import edu.tus.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final JwtUserDetailsService jwtUserDetailsService;

	public SecurityConfig(JwtUtil jwtUtil, JwtUserDetailsService jwtUserDetailsService) {
		this.jwtUtil = jwtUtil;
		this.jwtUserDetailsService = jwtUserDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		// Disable CSRF for simplicity in a REST API
		.csrf(csrf -> csrf.disable())
		// Use stateless session management
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(auth -> auth
				// Permit access to static resources and authentication endpoints
				.requestMatchers(
						"/", 
						"/index.html", 
						"/app.js", 
						"/favicon.ico", 
						"/css/**", 
						"/js/**", 
						"/images/**", 
						"/api/auth/**"
						).permitAll()

				// All other endpoints require authentication
				.anyRequest().authenticated()
				)
		.httpBasic(basic -> basic.disable())
		.formLogin(form -> form.disable());

		// Add custom JWT authentication filter before the default username/password filter
		http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, jwtUserDetailsService),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
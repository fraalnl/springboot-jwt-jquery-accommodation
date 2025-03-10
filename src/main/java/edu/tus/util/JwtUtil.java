package edu.tus.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

/*
JwtUtil – Creates and verifies JWT tokens.
JwtAuthenticationFilter – Checks the token before allowing access.
SecurityConfig – Defines the security rules of the app.
JwtUserDetailsService – Fetches user details from the database.

Token: Once the password is verified, the system generates a token (a JWT) 
used in subsequent requests so that the user doesn't have to send password each time
io.jsonwebtoken library for creating, signing, and parsing JWTs. 
java.security.Key for cryptographic key management
@Component register it as a bean, available for dependency injection
token might be stored in memory (a variable within your JavaScript code) 
rather than in a persistent storage mechanism like local storage or a cookie. 
When the page is refreshed, in-memory data is cleared
 */
@Component
public class JwtUtil {
// a secret key generated for the HS256 algorithm, sign the JWTs
    private final Key jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    //After token expires, log in again (or refresh the token) to obtain a new valid token
    private static final long EXPIRATION_MS = 86400000L; // 1 day

    public String generateToken(final String username, final String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(jwtSecretKey)
                .compact(); //Returns the token in its compact (string) form
    }

    // ensure not tampered with and hasn't expired
    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey) //parse the token using the same secret key
                .build()
                .parseClaimsJws(token);
            return true; //if parsing successful
        } catch (JwtException e) {
            return false;
        }
    }
    // extract username(subject) from the token
    public String getUsernameFromToken(final String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    //extract role from token
    public String getRoleFromToken(final String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("role"); // retrieve value of "role" claim and casts it to String
    }
}

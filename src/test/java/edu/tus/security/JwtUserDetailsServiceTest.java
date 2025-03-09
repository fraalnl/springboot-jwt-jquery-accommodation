package edu.tus.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.tus.dao.UserRepository;
import edu.tus.model.StudentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class JwtUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtUserDetailsService jwtUserDetailsService;

    private StudentEntity sampleStudent;

    @BeforeEach
    public void setUp() {
        // Setup a sample student entity.
        sampleStudent = new StudentEntity();
        sampleStudent.setUsername("student1");
        sampleStudent.setEmail("student1@example.com");
        // Simulate an already encoded password; value doesn't matter for the test.
        sampleStudent.setPassword("$2a$10$examplehashedpassword");
        // For example, student role as ROLE_STUDENT
        sampleStudent.setRole("ROLE_STUDENT");
    }

    @Test
    public void testLoadUserByUsername_Admin() {
        // When the username is "admin", the service should return a User with ROLE_ADMIN.
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("admin");
        assertNotNull(userDetails, "Admin user details should not be null");
        assertEquals("admin", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                   .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")),
                   "Admin should have ROLE_ADMIN authority");
    }

    @Test
    public void testLoadUserByUsername_StudentFound() {
        // Stub the repository to return a sample student.
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(sampleStudent));

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("student1");
        assertNotNull(userDetails, "Student user details should not be null");
        assertEquals("student1", userDetails.getUsername());
        assertEquals(sampleStudent.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                   .anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT")),
                   "Student should have ROLE_STUDENT authority");
    }

    @Test
    public void testLoadUserByUsername_StudentNotFound() {
        // Stub the repository to return empty.
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
            jwtUserDetailsService.loadUserByUsername("nonexistent")
        );
        String expectedMessage = "User not found with username: nonexistent";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage),
                "Expected error message to contain: " + expectedMessage);
    }
}

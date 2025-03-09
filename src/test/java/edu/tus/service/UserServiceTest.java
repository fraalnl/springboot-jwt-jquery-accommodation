package edu.tus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.tus.dao.UserRepository;
import edu.tus.dto.StudentDto;
import edu.tus.model.StudentEntity;
import edu.tus.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private UserService userService;
    
    private StudentDto studentDto;
    
    @BeforeEach
    public void setUp() {
        studentDto = new StudentDto();
        studentDto.setUsername("testuser");
        studentDto.setEmail("test@example.com");
        studentDto.setPassword("password123");
    }
    
    @Test
    public void testCreateStudent() {
        // Stub password encoding
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        // Stub token generation
        when(jwtUtil.generateToken("testuser", "ROLE_STUDENT")).thenReturn("dummyToken");
        
        String token = userService.createStudent(studentDto);
        
        // Verify that the userRepository's save method was called with a StudentEntity having the expected properties.
        ArgumentCaptor<StudentEntity> userCaptor = ArgumentCaptor.forClass(StudentEntity.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        
        StudentEntity savedUser = userCaptor.getValue();
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("encodedPassword123", savedUser.getPassword());
        assertEquals("ROLE_STUDENT", savedUser.getRole());
        
        // Verify that the returned token is as expected.
        assertEquals("dummyToken", token);
    }
}

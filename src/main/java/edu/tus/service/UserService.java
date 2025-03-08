package edu.tus.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.tus.dao.UserRepository;
import edu.tus.dto.StudentDto;
import edu.tus.model.StudentEntity;
import edu.tus.util.JwtUtil;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(final UserRepository userRepository,
                       final PasswordEncoder passwordEncoder,
                       final JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Creates a student account with role "STUDENT" and returns a JWT token.
     */
    public String createStudent(final StudentDto dto) {
        StudentEntity user = new StudentEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("ROLE_STUDENT");

        userRepository.save(user);

        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }
}

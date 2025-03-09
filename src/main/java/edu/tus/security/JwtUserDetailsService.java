package edu.tus.security;

import java.util.Collections;
import java.util.Optional;

import edu.tus.dao.UserRepository;
import edu.tus.model.StudentEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;  // Spring Security's User
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            String bcryptHashOfAdminPassword = "$2a$10$3RQVLqn0kat.Z3Q5EiP8gO2ktaAm5hXktwNffaySxXz3zkmDxD7d2";
            User securityUser = new User(
                    "admin",
                    bcryptHashOfAdminPassword,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
            return securityUser;
        } else {
            Optional<StudentEntity> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                StudentEntity student = userOpt.get();
                return new User(
                        student.getUsername(),
                        student.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority(student.getRole()))
                );
            } else {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        }
    }
}

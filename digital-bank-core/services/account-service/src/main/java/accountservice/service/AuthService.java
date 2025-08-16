package accountservice.service;

import accountservice.model.Role;
import accountservice.model.User;
import accountservice.repository.RoleRepository;
import accountservice.repository.UserRepository;
import accountservice.security.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostConstruct
    public void initRoles() {
        if (roleRepository.count() == 0) {
            Role customer = new Role();
            customer.setName("CUSTOMER");
            roleRepository.save(customer);

            Role admin = new Role();
            admin.setName("ADMIN");
            roleRepository.save(admin);

            Role auditor = new Role();
            auditor.setName("AUDITOR");
            roleRepository.save(auditor);
        }
    }

    public User registerUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        var roles = new HashSet<Role>();
        roles.add(roleRepository.findByName("CUSTOMER").orElseThrow());
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public String authenticateAndGenerateToken(String username, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
            return jwtUtil.generateToken(user);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }
}

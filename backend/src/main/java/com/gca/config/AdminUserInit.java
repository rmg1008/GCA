package com.gca.config;

import com.gca.domain.User;
import com.gca.repository.RoleRepository;
import com.gca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserInit {

    @Value("${admin.default-email}")
    private String adminEmail;

    @Value("${admin.default-password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initAdminUser(UserRepository userRepository, RoleRepository roleRepository,
                                           PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User user = new User ();
                user.setName("Administrador");
                user.setEmail(adminEmail);
                user.setPassword(passwordEncoder.encode(adminPassword));
                user.setRoles(roleRepository.getRoleByName("Admin"));
                userRepository.save(user);
            }
        };
    }
}

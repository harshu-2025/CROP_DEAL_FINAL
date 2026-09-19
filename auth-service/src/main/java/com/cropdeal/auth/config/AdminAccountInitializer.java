package com.cropdeal.auth.config;

import com.cropdeal.auth.entity.AuthUser;
import com.cropdeal.auth.entity.Role;
import com.cropdeal.auth.repository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${cropdeal.admin.email}")
    private String adminEmail;

    @Value("${cropdeal.admin.password}")
    private String adminPassword;

    public AdminAccountInitializer(AuthUserRepository authUserRepository,
                                   PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        AuthUser admin = authUserRepository.findByEmail(adminEmail)
                .orElseGet(AuthUser::new);

        boolean needsSave = admin.getId() == null;

        if (admin.getId() == null) {
            admin.setEmail(adminEmail);
            admin.setCreatedAt(LocalDateTime.now());
        }

        if (admin.getRole() != Role.ADMIN) {
            admin.setRole(Role.ADMIN);
            needsSave = true;
        }

        if (!admin.isActive()) {
            admin.setActive(true);
            needsSave = true;
        }

        if (admin.getPassword() == null || !passwordEncoder.matches(adminPassword, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(adminPassword));
            needsSave = true;
        }

        if (needsSave) {
            authUserRepository.save(admin);
        }
    }
}

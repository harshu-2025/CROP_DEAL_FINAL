package com.cropdeal.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.cropdeal.user.entity.Role;
import com.cropdeal.user.entity.UserProfile;

import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByAuthUserId(Long authUserId);

    Optional<UserProfile> findByEmail(String email);

    List<UserProfile> findByRole(Role role);

    List<UserProfile> findByRoleAndActiveTrue(Role role);
}
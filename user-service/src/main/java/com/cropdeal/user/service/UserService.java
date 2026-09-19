package com.cropdeal.user.service;



import java.util.List;

import com.cropdeal.user.dto.UserRequest;
import com.cropdeal.user.dto.UserResponse;
import com.cropdeal.user.entity.Role;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByAuthUserId(Long authUserId);

    List<UserResponse> getAllUsers();

    List<UserResponse> getUsersByRole(Role role);

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);

    UserResponse activateUser(Long id);

    UserResponse deactivateUser(Long id);
}
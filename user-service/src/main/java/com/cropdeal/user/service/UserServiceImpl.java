package com.cropdeal.user.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import com.cropdeal.user.dto.UserRequest;
import com.cropdeal.user.dto.UserResponse;
import com.cropdeal.user.entity.Role;
import com.cropdeal.user.entity.UserProfile;
import com.cropdeal.user.exception.UserNotFoundException;
import com.cropdeal.user.repository.UserRepository;
import com.cropdeal.user.security.SecurityUtils;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

	@Override
	public UserResponse createUser(UserRequest request) {
        if (!SecurityUtils.hasRole("ADMIN")) {
            if (!SecurityUtils.getCurrentAuthUserId().equals(request.getAuthUserId())) {
                throw new AccessDeniedException("Cannot create a profile for another user");
            }
            if (!SecurityUtils.hasRole(request.getRole().name())) {
                throw new AccessDeniedException("Profile role must match authenticated role");
            }
        }

		if(userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email Id already exixt");
			}
        if (userRepository.findByAuthUserId(request.getAuthUserId()).isPresent()) {
            throw new IllegalArgumentException("User profile already exists for this auth user");
        }
	UserProfile user = new UserProfile();
	
	user.setAuthUserId(request.getAuthUserId());
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPhone(request.getPhone());
    user.setAddress(request.getAddress());
    user.setCity(request.getCity());
    user.setState(request.getState());
    user.setRole(request.getRole());
    user.setBankAccountNumber(request.getBankAccountNumber());
    user.setBankName(request.getBankName());
    user.setIfscCode(request.getIfscCode());

    user.setActive(true);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    
    UserProfile saveUser = userRepository.save(user);
    
    return mapToResponse(saveUser);
    
	}

	@Override
	public UserResponse getUserById(Long id) {
		UserProfile user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                "User not found with id: " + id));
		return mapToResponse(user);
	}

	@Override
	public UserResponse getUserByAuthUserId(Long authUserId) {
		UserProfile user = userRepository.findByAuthUserId(authUserId).orElseThrow(()-> new UserNotFoundException(
				"User Not Found with"+authUserId));
		return mapToResponse(user);
	}

	@Override
    public List<UserResponse> getAllUsers() {

        requireAdmin();
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<UserResponse> getUsersByRole(Role role) {

        requireAdmin();
        return userRepository.findByRole(role)
                .stream()
                .map(this::mapToResponse)
                .toList();
	}

	@Override
	public UserResponse updateUser(Long id, UserRequest request) {
		UserProfile user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id));

        ensureOwnerOrAdmin(user);

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        if (SecurityUtils.hasRole("ADMIN")) {
            user.setRole(request.getRole());
        }
        user.setBankAccountNumber(request.getBankAccountNumber());
        user.setBankName(request.getBankName());
        user.setIfscCode(request.getIfscCode());

        user.setUpdatedAt(LocalDateTime.now());

        UserProfile updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
	}

	@Override
	public void deleteUser(Long id) {
        requireAdmin();
		  if (!userRepository.existsById(id)) {
	            throw new UserNotFoundException(
	                    "User not found with id: " + id);
	        }

	        userRepository.deleteById(id);
	}

	  @Override
	    public UserResponse activateUser(Long id) {

            requireAdmin();
	        UserProfile user = userRepository.findById(id)
	                .orElseThrow(() ->
	                        new UserNotFoundException(
	                                "User not found with id: " + id));

	        user.setActive(true);
	        user.setUpdatedAt(LocalDateTime.now());

	        return mapToResponse(userRepository.save(user));
	    }

	    @Override
	    public UserResponse deactivateUser(Long id) {

            requireAdmin();
	        UserProfile user = userRepository.findById(id)
	                .orElseThrow(() ->
	                        new UserNotFoundException(
	                                "User not found with id: " + id));

	        user.setActive(false);
	        user.setUpdatedAt(LocalDateTime.now());

	        return mapToResponse(userRepository.save(user));
	    }
	    
    private void ensureOwnerOrAdmin(UserProfile user) {
        if (!SecurityUtils.hasRole("ADMIN") &&
                !SecurityUtils.getCurrentAuthUserId().equals(user.getAuthUserId())) {
            throw new AccessDeniedException("You can access only your own profile");
        }
    }

    private void requireAdmin() {
        if (!SecurityUtils.hasRole("ADMIN")) {
            throw new AccessDeniedException("Admin access required");
        }
    }

	private UserResponse mapToResponse(UserProfile user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setAuthUserId(user.getAuthUserId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setCity(user.getCity());
        response.setState(user.getState());
        response.setRole(user.getRole());
        response.setBankAccountNumber(user.getBankAccountNumber());
        response.setBankName(user.getBankName());
        response.setIfscCode(user.getIfscCode());
        response.setActive(user.isActive());

        return response;
    }

	

}

package com.shiptrack.service;

import com.shiptrack.dto.ChangePasswordRequest;
import com.shiptrack.dto.UpdateProfileRequest;
import com.shiptrack.dto.UpdateProfileResponse;
import com.shiptrack.dto.UserProfileResponse;
import com.shiptrack.entity.User;
import com.shiptrack.exception.UserAlreadyExistsException;
import com.shiptrack.exception.UserNotFoundException;
import com.shiptrack.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public SettingsService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    


    private UserProfileResponse convertToProfileResponse(User user) {

        UserProfileResponse response = new UserProfileResponse();

        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());

        if (user.getRole() != null) {
            response.setRole(user.getRole().getName());
        }

        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }

    


    public UserProfileResponse getProfile() {

        User user = getCurrentUser();

        return convertToProfileResponse(user);
    }

    


    public UpdateProfileResponse updateProfile(UpdateProfileRequest request) {

        User user = getCurrentUser();

        
        if (request.getFullName() != null &&
                !request.getFullName().trim().isEmpty()) {

            user.setFullName(request.getFullName().trim());
        }

        
        if (request.getEmail() != null &&
                !request.getEmail().trim().isEmpty()) {

            String newEmail =
                    request.getEmail()
                            .trim()
                            .toLowerCase();

            if (!newEmail.equals(user.getEmail())) {

                if (!newEmail.contains("@")
                        || newEmail.startsWith("@")
                        || newEmail.endsWith("@")) {

                    throw new IllegalArgumentException(
                            "Enter a valid email ID.");
                }

                userRepository.findByEmail(newEmail)
                        .ifPresent(existing -> {
                            throw new UserAlreadyExistsException(
                                    "An account with this email already exists.");
                        });

                user.setEmail(newEmail);
            }
        }

        
        if (request.getPhone() != null &&
                !request.getPhone().trim().isEmpty()) {

            user.setPhone(request.getPhone().trim());
        }

        
        userRepository.save(user);

        String token =
                jwtService.generateToken(
                        user.getEmail());

        return new UpdateProfileResponse(
                convertToProfileResponse(user),
                token);
    }

    


    public void changePassword(ChangePasswordRequest request) {

        User user = getCurrentUser();

        
        if (request.getCurrentPassword() == null ||
                request.getCurrentPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Current password is required.");
        }

        if (request.getNewPassword() == null ||
                request.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("New password is required.");
        }

        if (request.getConfirmPassword() == null ||
                request.getConfirmPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Confirm password is required.");
        }

        
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException("Current password is incorrect.");
        }

        
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "New password and confirm password do not match.");
        }

        
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password cannot be the same as the current password.");
        }

        
        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
}


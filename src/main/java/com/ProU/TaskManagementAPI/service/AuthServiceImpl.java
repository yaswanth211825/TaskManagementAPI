package com.ProU.TaskManagementAPI.service;

import com.ProU.TaskManagementAPI.DTO.AuthRequest;
import com.ProU.TaskManagementAPI.DTO.AuthResponse;
import com.ProU.TaskManagementAPI.Services.AuthService;
import com.ProU.TaskManagementAPI.entity.UserAccount;
import com.ProU.TaskManagementAPI.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        if (request == null) return null;
        Optional<UserAccount> opt = userRepository.findByUsername(request.username());
        if (opt.isEmpty()) return null;
        UserAccount user = opt.get();
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return null;
        }
        // For now, token is simple representation; Step 3 will replace with JWT
        String token = user.getUsername() + ":" + user.getRole();
        return new AuthResponse(token, user.getRole());
    }
}


package edu.cit.capendit.unisell.auth.service;

import edu.cit.capendit.unisell.admin.activitylog.model.ActivityActionType;
import edu.cit.capendit.unisell.admin.activitylog.service.ActivityLogService;
import edu.cit.capendit.unisell.auth.dto.AuthResponse;
import edu.cit.capendit.unisell.auth.dto.LoginRequest;
import edu.cit.capendit.unisell.auth.dto.RegisterRequest;
import edu.cit.capendit.unisell.auth.model.Role;
import edu.cit.capendit.unisell.auth.model.User;
import edu.cit.capendit.unisell.auth.repository.UserRepository;
import edu.cit.capendit.unisell.core.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ActivityLogService activityLogService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.VENDOR);

        userRepository.save(user);

        activityLogService.log(user.getEmail(), user.getRole().name(),
                ActivityActionType.USER_REGISTERED, "New vendor account registered",
                "USER", user.getId());

        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail()).orElse(null);

        if (user == null) {
            activityLogService.log(req.getEmail(), null,
                    ActivityActionType.LOGIN_FAILURE, "Login failed: no account found for this email");
            throw new RuntimeException("Invalid credentials");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            activityLogService.log(user.getEmail(), user.getRole().name(),
                    ActivityActionType.LOGIN_FAILURE, "Login failed: incorrect password");
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isEnabled()) {
            activityLogService.log(user.getEmail(), user.getRole().name(),
                    ActivityActionType.LOGIN_FAILURE, "Login failed: account is deactivated");
            throw new RuntimeException("This account has been deactivated. Contact support.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        activityLogService.log(user.getEmail(), user.getRole().name(),
                ActivityActionType.LOGIN_SUCCESS, "Login successful");

        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name(), token);
    }
}
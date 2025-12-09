package uz.pdp.ecommerce2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ecommerce2.config.JwtUtil;
import uz.pdp.ecommerce2.dto.AuthResponse;
import uz.pdp.ecommerce2.dto.LoginRequest;
import uz.pdp.ecommerce2.dto.RegisterRequest;
import uz.pdp.ecommerce2.exception.DuplicateResourceException;
import uz.pdp.ecommerce2.exception.ResourceNotFoundException;
import uz.pdp.ecommerce2.mapper.UserMapper;
import uz.pdp.ecommerce2.model.Role;
import uz.pdp.ecommerce2.model.User;
import uz.pdp.ecommerce2.repository.UserRepository;

// AUTH SERVICE
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        User user = userMapper.registerRequestToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user);
        AuthResponse response = userMapper.userToAuthResponse(user);
        response.setToken(token);
        
        return response;
    }
    
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String token = jwtUtil.generateToken(user);
        AuthResponse response = userMapper.userToAuthResponse(user);
        response.setToken(token);
        
        return response;
    }
    @Transactional
    public AuthResponse createAdmin(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = userMapper.registerRequestToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_ADMIN);  // Устанавливаем роль ADMIN
        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        AuthResponse response = userMapper.userToAuthResponse(user);
        response.setToken(token);

        return response;
    }
}

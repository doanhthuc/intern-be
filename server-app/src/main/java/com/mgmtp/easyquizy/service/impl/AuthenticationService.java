package com.mgmtp.easyquizy.service.impl;

import com.mgmtp.easyquizy.mapper.UserMapper;
import com.mgmtp.easyquizy.model.auth.AuthenticationRequest;
import com.mgmtp.easyquizy.model.auth.AuthenticationResponse;
import com.mgmtp.easyquizy.model.auth.RegisterRequest;
import com.mgmtp.easyquizy.model.role.RoleEntity;
import com.mgmtp.easyquizy.model.user.UserEntity;
import com.mgmtp.easyquizy.repository.RoleRepository;
import com.mgmtp.easyquizy.repository.UserRepository;
import com.mgmtp.easyquizy.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserMapper userMapper;

    public AuthenticationResponse register(RegisterRequest request) {
        Optional<UserEntity> existedUser = userRepository.findByUsername(request.getUsername());
        if (existedUser.isPresent()) {
            throw new RuntimeException("Username already exist");
        }

        List<RoleEntity> rolesRegister = roleRepository.findAllByIdIsIn(request.getRoleIds());
        String passwordEncode = passwordEncoder.encode(request.getPassword());
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .name(request.getName())
                .password(passwordEncode)
                .roles(rolesRegister)
                .build();
        UserEntity savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .user(userMapper.entityToUserDTO(savedUser))
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new RuntimeException("Authentication failed");
        }
        Optional<UserEntity> user = userRepository.findByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(user.get());
        return AuthenticationResponse.builder()
                .user(userMapper.entityToUserDTO(user.get()))
                .accessToken(jwtToken)
                .build();
    }
}

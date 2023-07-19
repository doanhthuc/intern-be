package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.auth.AuthenticationRequestDTO;
import com.mgmtp.easyquizy.dto.auth.AuthenticationResponseDTO;
import com.mgmtp.easyquizy.dto.auth.RegisterRequestDTO;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.mapper.UserMapper;
import com.mgmtp.easyquizy.model.role.RoleEntity;
import com.mgmtp.easyquizy.model.user.UserEntity;
import com.mgmtp.easyquizy.repository.RoleRepository;
import com.mgmtp.easyquizy.repository.UserRepository;
import com.mgmtp.easyquizy.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
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

    @Override
    public AuthenticationResponseDTO register(RegisterRequestDTO request) {
        Optional<UserEntity> existedUser = userRepository.findByUsername(request.getUsername());
        if (existedUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
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
        return AuthenticationResponseDTO.builder()
                .user(userMapper.entityToUserDTO(savedUser))
                .accessToken(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username or password");
        }
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> InvalidFieldsException.fromFieldError("username", "There is no user with that username!"));
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDTO.builder()
                .user(userMapper.entityToUserDTO(user))
                .accessToken(jwtToken)
                .build();
    }
}

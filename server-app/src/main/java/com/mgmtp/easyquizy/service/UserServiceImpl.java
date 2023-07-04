package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.mapper.UserMapper;
import com.mgmtp.easyquizy.model.auth.ChangePasswordRequest;
import com.mgmtp.easyquizy.model.user.UserEntity;
import com.mgmtp.easyquizy.repository.UserRepository;
import com.mgmtp.easyquizy.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return user.get();
    }

    @Override
    @Transactional
    public UserEntity loadUserById(Long userId) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (userEntityOptional.isEmpty()) {
            throw new UsernameNotFoundException("Failed");
        }
        return userEntityOptional.get();
    }

    @Override
    public void changePassword(UserEntity user,ChangePasswordRequest request) {
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect.");
        }
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new RuntimeException("New password must be different from current password");
        }
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }
}
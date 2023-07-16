package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.user.UserDTO;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.model.auth.ChangePasswordRequest;
import com.mgmtp.easyquizy.model.user.UserEntity;
import org.springframework.data.domain.Page;

public interface UserService {
    UserEntity loadUserById(Long userId);

    void changePassword(UserEntity user, ChangePasswordRequest request);

    String resetPassword(Long id);

    UserDTO getUserById(Long id);

    Page<UserDTO> getAllUsers(String keyword, int offset, int limit);

    UserDTO updateUser(UserDTO userDTO);

    void deleteUserById(Long id) throws RecordNotFoundException;
}

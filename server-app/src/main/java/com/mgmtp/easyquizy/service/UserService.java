package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.model.auth.ChangePasswordRequest;
import com.mgmtp.easyquizy.model.user.UserEntity;

public interface UserService {
    UserEntity loadUserById(Long userId);

    void changePassword(UserEntity user, ChangePasswordRequest request);
}

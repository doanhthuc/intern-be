package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.auth.AuthenticationRequestDTO;
import com.mgmtp.easyquizy.dto.auth.AuthenticationResponseDTO;
import com.mgmtp.easyquizy.dto.auth.RegisterRequestDTO;

public interface AuthenticationService {
    AuthenticationResponseDTO register(RegisterRequestDTO request);

    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);
}

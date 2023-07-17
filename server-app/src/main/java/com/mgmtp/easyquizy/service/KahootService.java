package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.kahoot.KahootFolderDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserStatusResponseDto;
import com.mgmtp.easyquizy.model.auth.AuthenticationRequest;
import com.mgmtp.easyquizy.model.kahoot.KahootAccountEntity;

public interface KahootService {
    KahootAccountEntity getKahootAccount();

    KahootUserStatusResponseDto authenticate(AuthenticationRequest authenticationRequest);

    KahootFolderDTO getOrCreateFolder(String folderName);
}

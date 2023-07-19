package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.kahoot.KahootAuthenticationRequestDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootExportQuizResponseDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootFolderDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserStatusResponseDto;
import com.mgmtp.easyquizy.model.kahoot.KahootAccountEntity;

public interface KahootService {
    KahootAccountEntity getKahootAccount();

    KahootUserStatusResponseDto authenticate(KahootAuthenticationRequestDTO authenticationRequest);

    KahootUserStatusResponseDto logout();

    KahootFolderDTO createFolder(String folderName);

    KahootFolderDTO getOrCreateFolder(String folderName);

    KahootExportQuizResponseDTO exportQuiz(long id);
}

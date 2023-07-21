package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.kahoot.KahootAuthenticationRequestDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootFolderDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserStatusResponseDto;
import com.mgmtp.easyquizy.model.attachment.AttachmentEntity;
import com.mgmtp.easyquizy.model.kahoot.KahootAccountEntity;

import java.util.concurrent.ConcurrentMap;

public interface KahootService {
    KahootAccountEntity getKahootAccount();

    KahootUserStatusResponseDto authenticate(KahootAuthenticationRequestDTO authenticationRequest);

    KahootUserStatusResponseDto logout();

    KahootFolderDTO createFolder(String folderName);

    KahootFolderDTO getOrCreateFolder(String folderName);

    void exportQuiz(long id);

    ConcurrentMap<Long, String> uploadImages(AttachmentEntity[] attachments, String token);
}

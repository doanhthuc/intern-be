package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.kahoot.KahootAuthenticationRequestDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootFolderDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserStatusResponseDto;
import com.mgmtp.easyquizy.model.attachment.AttachmentEntity;
import com.mgmtp.easyquizy.model.kahoot.KahootAccountEntity;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ConcurrentMap;

public interface KahootService {
    KahootAccountEntity getKahootAccount();

    KahootUserStatusResponseDto authenticate(KahootAuthenticationRequestDTO authenticationRequest);

    KahootUserStatusResponseDto logout();

    KahootFolderDTO createFolder(String folderName);

    KahootFolderDTO getOrCreateFolder(String folderName);

    SecretKey generateSecretKey(byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchProviderException;

    KahootAccountEntity getUserByExistingCredentials(String username, String password);
    String encryptPassword(String plaintext, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException;

    String decryptPassword(String ciphertext, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException;

    void exportQuiz(long id);

    ConcurrentMap<Long, String> uploadImages(AttachmentEntity[] attachments, String token);

    void deleteKahootQuiz(long quizId);
}

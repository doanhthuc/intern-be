package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.kahoot.KahootFolderDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserRequestDto;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserResponseDto;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserStatusResponseDto;
import com.mgmtp.easyquizy.exception.HttpErrorStatusException;
import com.mgmtp.easyquizy.mapper.KahootAccountMapper;
import com.mgmtp.easyquizy.model.auth.AuthenticationRequest;
import com.mgmtp.easyquizy.model.kahoot.KahootAccountEntity;
import com.mgmtp.easyquizy.repository.KahootAccountRepository;
import com.mgmtp.easyquizy.utils.RestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KahootServiceImpl implements KahootService {
    private static final String LOG_IN_URL = "https://create.kahoot.it/rest/authenticate";
    private static final String FOLDER_URL_TEMPLATE = "https://create.kahoot.it/rest/folders/%s";

    private final KahootAccountRepository kahootAccountRepository;

    private final KahootAccountMapper kahootAccountMapper;

    @Override
    public KahootAccountEntity getKahootAccount() {
        Optional<KahootAccountEntity> kahootAccountEntityOptional = kahootAccountRepository.findFirstByOrderByUuid();
        if (kahootAccountEntityOptional.isEmpty()) {
            return null;
        }
        KahootAccountEntity kahootAccountEntity = kahootAccountEntityOptional.get();
        if (kahootAccountEntity.getExpireTime() < System.currentTimeMillis()) {
            kahootAccountRepository.deleteById(kahootAccountEntity.getUuid());
            return null;
        }
        try {
            new RestClient()
                    .setBearerToken(kahootAccountEntity.getAccessToken())
                    .setContentType(MediaType.APPLICATION_JSON_VALUE)
                    .setUrl(LOG_IN_URL)
                    .setMethod("GET")
                    .call(KahootUserResponseDto.class);
        } catch (HttpErrorStatusException e) {
            if (e.getStatusCode() == 401) {
                kahootAccountRepository.deleteById(kahootAccountEntity.getUuid());
                return null;
            }
        }
        return kahootAccountEntity;
    }

    @Override
    public KahootUserStatusResponseDto authenticate(AuthenticationRequest authenticationRequest) {
        KahootUserRequestDto kahootUserRequestDto = KahootUserRequestDto.builder()
                .username(authenticationRequest.getUsername())
                .password(authenticationRequest.getPassword())
                .grant_type("password")
                .build();
        KahootUserResponseDto responseBody = null;
        try {
            responseBody = new RestClient()
                    .setJsonRequestBody(kahootUserRequestDto)
                    .setContentType(MediaType.APPLICATION_JSON_VALUE)
                    .setUrl(LOG_IN_URL)
                    .setMethod("POST")
                    .call(KahootUserResponseDto.class);
        } catch (HttpErrorStatusException e) {
            if (e.getStatusCode() == 401) {
                throw new IllegalStateException("Username or password error!");
            }
        }
        KahootAccountEntity kahootAccountEntity =
                kahootAccountRepository.save(kahootAccountMapper.userResponseDtoToAccountEntity(responseBody));
        KahootUserStatusResponseDto userStatusResponseDto =
                kahootAccountMapper.kahootAccountEntityToUserStatusDto(kahootAccountEntity);
        userStatusResponseDto.setIsConnected(true);
        return userStatusResponseDto;
    }

    @Override
    public KahootUserStatusResponseDto logout() {
        KahootAccountEntity kahootAccount = getKahootAccount();
        if (kahootAccount != null) {
            kahootAccountRepository.deleteById(kahootAccount.getUuid());
        }
        return KahootUserStatusResponseDto.getDisconnectDto();
    }

    @Override
    public KahootFolderDTO createFolder(String folderName) {
        KahootAccountEntity kahootAccount = getKahootAccount();
        if (kahootAccount == null) {
            throw new IllegalStateException("No valid Kahoot account found!");
        }
        String rootFolderUrl = String.format(FOLDER_URL_TEMPLATE, kahootAccount.getUuid());
        try {
            return new RestClient()
                    .setBearerToken(kahootAccount.getAccessToken())
                    .setContentType(MediaType.APPLICATION_JSON_VALUE)
                    .setUrl(rootFolderUrl + "/folders")
                    .setMethod("POST")
                    .setJsonRequestBody(Collections.singletonMap("name", folderName))
                    .call(KahootFolderDTO.class);
        } catch (HttpErrorStatusException ex) {
            throw new IllegalStateException("Failed to create new Kahoot folder!");
        }
    }

    @Override
    public KahootFolderDTO getFolder(String folderName) {
        KahootAccountEntity kahootAccount = getKahootAccount();
        if (kahootAccount == null) {
            throw new IllegalStateException("No valid Kahoot account found!");
        }
        String rootFolderUrl = String.format(FOLDER_URL_TEMPLATE, kahootAccount.getUuid());
        KahootFolderDTO kahootFolderDTO = null;
        try {
            kahootFolderDTO = new RestClient()
                    .setBearerToken(kahootAccount.getAccessToken())
                    .setContentType(MediaType.APPLICATION_JSON_VALUE)
                    .setUrl(rootFolderUrl)
                    .setMethod("GET")
                    .call(KahootFolderDTO.class);
        } catch (HttpErrorStatusException e) {
            throw new IllegalStateException("Failed to retrieve Kahoot folder information!");
        }
        Optional<KahootFolderDTO> existingFolder = Optional.ofNullable(kahootFolderDTO)
                .orElseThrow(() -> new IllegalStateException("No valid Kahoot folder found!"))
                .getFolders().stream()
                .filter(folder -> folder.getName().equals(folderName))
                .findFirst();
        return existingFolder.orElseGet(() -> createFolder(folderName));
    }
}

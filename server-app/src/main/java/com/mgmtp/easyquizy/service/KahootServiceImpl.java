package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.kahoot.KahootFolderDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserRequestDto;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserResponseDto;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserStatusResponseDto;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.mapper.KahootAccountMapper;
import com.mgmtp.easyquizy.model.auth.AuthenticationRequest;
import com.mgmtp.easyquizy.model.kahoot.KahootAccountEntity;
import com.mgmtp.easyquizy.repository.KahootAccountRepository;
import com.mgmtp.easyquizy.utils.RestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

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
                    .defaultHeader()
                    .setBearerToken(kahootAccountEntity.getAccessToken())
                    .setUrl(LOG_IN_URL)
                    .setMethod(HttpMethod.GET)
                    .call(KahootUserResponseDto.class);
        } catch (HttpStatusCodeException ex) {
            if (ex.getRawStatusCode() == 401) {
                kahootAccountRepository.deleteById(kahootAccountEntity.getUuid());
                return null;
            }
        }
        return kahootAccountEntity;
    }

    @Override
    public KahootUserStatusResponseDto authenticate(AuthenticationRequest authenticationRequest) {
        try {
            KahootUserRequestDto kahootUserRequestDto = KahootUserRequestDto.builder()
                    .username(authenticationRequest.getUsername())
                    .password(authenticationRequest.getPassword())
                    .grant_type("password")
                    .build();
            ResponseEntity<KahootUserResponseDto> responseEntity = new RestClient()
                    .defaultHeader()
                    .setRequestBody(kahootUserRequestDto)
                    .setUrl(LOG_IN_URL)
                    .setMethod(HttpMethod.POST)
                    .call(KahootUserResponseDto.class);
            KahootUserResponseDto responseBody = responseEntity.getBody();
            KahootAccountEntity kahootAccountEntity =
                    kahootAccountRepository.save(kahootAccountMapper.userResponseDtoToAccountEntity(responseBody));

            KahootUserStatusResponseDto userStatusResponseDto =
                    kahootAccountMapper.kahootAccountEntityToUserStatusDto(kahootAccountEntity);
            userStatusResponseDto.setIsConnected(true);
            return userStatusResponseDto;
        } catch (HttpStatusCodeException ex) {
            throw InvalidFieldsException.fromFieldError("Message", "Username or password error");
        }
    }

    @Override
    public KahootFolderDTO getOrCreateFolder(String folderName) {
        KahootAccountEntity kahootAccount = getKahootAccount();
        if (kahootAccount == null) {
            throw new IllegalStateException("No valid Kahoot account found!");
        }

        String rootFolderUrl = String.format(FOLDER_URL_TEMPLATE, kahootAccount.getUuid());

        RestClient restClient = new RestClient()
                .defaultHeader()
                .setBearerToken(kahootAccount.getAccessToken());

        KahootFolderDTO kahootFolderDTO;
        try {
            kahootFolderDTO = restClient.setUrl(rootFolderUrl)
                    .setMethod(HttpMethod.GET)
                    .call(KahootFolderDTO.class)
                    .getBody();
        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException("Failed to retrieve Kahoot folder information!");
        }

        Optional<KahootFolderDTO> existingFolder = Optional.ofNullable(kahootFolderDTO)
                .orElseThrow(() -> new IllegalStateException("No valid Kahoot folder found!"))
                .getFolders().stream()
                .filter(f -> f.getName().equals(folderName))
                .findFirst();

        if (existingFolder.isPresent()) {
            return existingFolder.get();
        } else {
            try {
                return
                        restClient.setUrl(rootFolderUrl + "/folders")
                                .setMethod(HttpMethod.POST)
                                .setRequestBody(Collections.singletonMap("name", folderName))
                                .call(KahootFolderDTO.class)
                                .getBody();
            } catch (HttpStatusCodeException e) {
                throw new IllegalStateException("Failed to create new Kahoot folder!");
            }
        }
    }
}

package com.mgmtp.easyquizy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mgmtp.easyquizy.dto.kahoot.*;
import com.mgmtp.easyquizy.exception.HttpErrorStatusException;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.exception.KahootExportQuizException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.exception.kahoot.*;
import com.mgmtp.easyquizy.mapper.KahootAccountMapper;
import com.mgmtp.easyquizy.mapper.KahootQuizMapper;
import com.mgmtp.easyquizy.model.attachment.AttachmentEntity;
import com.mgmtp.easyquizy.model.event.EventEntity;
import com.mgmtp.easyquizy.model.kahoot.ExportStatus;
import com.mgmtp.easyquizy.model.kahoot.KahootAccountEntity;
import com.mgmtp.easyquizy.model.kahoot.KahootQuizExportStatus;
import com.mgmtp.easyquizy.model.kahoot.QuizUserId;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import com.mgmtp.easyquizy.model.quiz.QuizEntity;
import com.mgmtp.easyquizy.repository.*;
import com.mgmtp.easyquizy.utils.ConvertBase64ToFile;
import com.mgmtp.easyquizy.utils.RestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KahootServiceImpl implements KahootService {
    private static final String LOG_IN_URL = "https://create.kahoot.it/rest/authenticate";
    private static final String FOLDER_URL_TEMPLATE = "https://create.kahoot.it/rest/folders/%s";
    private static final String CREATE_QUIZ_DRAFT_URL = "https://create.kahoot.it/rest/drafts";
    private static final String PUBLISH_QUIZ_DRAFT_URL = "https://create.kahoot.it/rest/drafts/%s/publish";
    private static final String DELETE_QUIZ_URL = "https://create.kahoot.it/rest/kahoots/%s";
    private static final String UPLOAD_IMAGE_URL = "https://apis.kahoot.it/media-api/media/upload";

    @Value("${easy-quizy.api.max-upload-image-thread}")
    private int MAX_UPLOAD_IMAGE_THREAD;

    @Value("${easy-quizy.api.max-upload-image-timeout-seconds}")
    private int MAX_UPLOAD_IMAGE_TIMEOUT;

    private final KahootAccountRepository kahootAccountRepository;

    private final QuizRepository quizRepository;

    private final EventRepository eventRepository;

    private final KahootQuizExportStatusRepository kahootQuizExportStatusRepository;

    private final QuestionRepository questionRepository;

    private final KahootAccountMapper kahootAccountMapper;

    private final KahootQuizMapper kahootQuizMapper;

    @Value("${kahoot.secret-key}")
    private String kahootSecretKey;

    @Value("${kahoot.AES_ALGORITHM}")
    private String AES_ALGORITHM;

    @Value("${kahoot.AES_PROVIDER}")
    private String AES_PROVIDER;

    /**
     * Retrieves a Kahoot account from the repository.
     *
     * @return a KahootAccountEntity object representing the Kahoot account, or null if no valid account is found
     */
    @Override
    public KahootAccountEntity getKahootAccount() {
        Optional<KahootAccountEntity> kahootAccountEntityOptional = kahootAccountRepository.findFirstByOrderByUuid();
        if (kahootAccountEntityOptional.isEmpty()) {
            return null;
        }
        KahootAccountEntity kahootAccountEntity = kahootAccountEntityOptional.get();
        String kahootUsername = kahootAccountEntity.getUsername();
        String kahootPassword = kahootAccountEntity.getKahootPassword();
        String kahootToken = kahootAccountEntity.getAccessToken();

        try {
            new RestClient()
                    .setBearerToken(kahootToken)
                    .setContentType(MediaType.APPLICATION_JSON_VALUE)
                    .setUrl(LOG_IN_URL)
                    .setMethod("GET")
                    .call(KahootUserResponseDto.class);
        } catch (HttpErrorStatusException e) {
            if (e.getStatusCode() / 100 == 4) {
                KahootAccountEntity getKahootByCredentials = getUserByExistingCredentials(kahootUsername, kahootPassword);
                if (getKahootByCredentials != null) {
                    kahootAccountEntity = getKahootByCredentials;
                } else {
                    return null;
                }
            }
        }
        return kahootAccountEntity;
    }

    @Override
    public KahootAccountEntity getUserByExistingCredentials(String kahootUsername, String kahootPassword) {
        byte[] keyBytes = DatatypeConverter.parseHexBinary(kahootSecretKey);
        KahootAccountEntity kahootAccountEntity;
        try {
            SecretKey secretKey = generateSecretKey(keyBytes);
            String decryptedPassword = decryptPassword(kahootPassword, secretKey);
            KahootAuthenticationRequestDTO authenticationRequest = new KahootAuthenticationRequestDTO(kahootUsername, decryptedPassword);
            authenticate(authenticationRequest);
            kahootAccountEntity = kahootAccountRepository.findFirstByOrderByUuid().orElse(null);
        } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException | NoSuchPaddingException | NoSuchProviderException ex) {
            throw new EncryptionException("Error occurred during encryption or saving account", ex);
        }
        return kahootAccountEntity;
    }

    @Override
    public SecretKey generateSecretKey(byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    @Override
    public String encryptPassword(String plaintext, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM, AES_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decryptPassword(String ciphertext, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM, AES_PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    @Override
    public KahootUserStatusResponseDto authenticate(KahootAuthenticationRequestDTO authenticationRequest) {
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
                kahootAccountRepository.deleteAll();
                throw new KahootUnauthorizedException();
            }
        }

        KahootAccountEntity kahootAccountEntity = kahootAccountMapper.userResponseDtoToAccountEntity(responseBody);

        byte[] keyBytes = DatatypeConverter.parseHexBinary(kahootSecretKey);
        try {
            SecretKey secretKey = generateSecretKey(keyBytes);
            String encryptedPassword = encryptPassword(authenticationRequest.getPassword(), secretKey);
            kahootAccountEntity.setKahootPassword(encryptedPassword);
            kahootAccountRepository.deleteAll();
            kahootAccountRepository.save(kahootAccountEntity);
        } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException | NoSuchPaddingException | NoSuchProviderException e) {
            throw new EncryptionException("Error occurred during encryption or saving account", e);
        }

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
        KahootAccountEntity kahootAccount = validateKahootAccount();
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
            throw new KahootException("Failed to create new Kahoot folder!");
        }
    }

    @Override
    @SuppressWarnings("squid:S1860")
    public KahootFolderDTO getOrCreateFolder(String folderName) {
        synchronized (folderName.intern()) {
            KahootAccountEntity kahootAccount = validateKahootAccount();
            String rootFolderUrl = String.format(FOLDER_URL_TEMPLATE, kahootAccount.getUuid());
            KahootFolderDTO kahootFolderDTO;
            try {
                kahootFolderDTO = new RestClient()
                        .setBearerToken(kahootAccount.getAccessToken())
                        .setContentType(MediaType.APPLICATION_JSON_VALUE)
                        .setUrl(rootFolderUrl)
                        .setMethod("GET")
                        .call(KahootFolderDTO.class);
            } catch (HttpErrorStatusException e) {
                throw new KahootException("Failed to retrieve Kahoot folder information!");
            }
            Optional<KahootFolderDTO> existingFolder = Optional.ofNullable(kahootFolderDTO)
                    .orElseThrow(KahootUnauthorizedException::new)
                    .getFolders().stream()
                    .filter(folder -> folder.getName().equals(folderName))
                    .findFirst();
            return existingFolder.orElseGet(() -> createFolder(folderName));
        }
    }

    private KahootExportQuizResponseDTO publishDraft(RestClient restClient, String draftID) {
        try {
            JsonNode response = restClient.setUrl(String.format(PUBLISH_QUIZ_DRAFT_URL, draftID))
                    .setMethod("POST")
                    .call(JsonNode.class);
            return new KahootExportQuizResponseDTO(response.get("uuid").asText());
        } catch (HttpErrorStatusException e) {
            throw new KahootPublishDraftException();
        }
    }

    private KahootExportQuizResponseDTO createAndPublishQuiz(RestClient restClient, KahootCreateDraftRequestDTO kahootCreateDraftRequestDTO) {
        try {
            JsonNode draftResponse = restClient.setUrl(CREATE_QUIZ_DRAFT_URL)
                    .setMethod("POST")
                    .setJsonRequestBody(kahootCreateDraftRequestDTO)
                    .call(JsonNode.class);

            String draftID = draftResponse.get("id").asText();
            return publishDraft(restClient, draftID);
        } catch (HttpErrorStatusException e) {
            throw new KahootCreateDraftException();
        }
    }

    @Override
    public void exportQuiz(long id) {
        KahootAccountEntity kahootAccount = validateKahootAccount();

        Optional<QuizEntity> quiz = quizRepository.findById(id);
        if (quiz.isEmpty()) {
            throw new RecordNotFoundException("The quiz with id " + id + " does not exist");
        }

        KahootQuizExportStatus existingKahootQuizExportStatus = getKahootQuizExportStatus(id);
        if (existingKahootQuizExportStatus != null) {
            throw new KahootExportQuizException("This quiz is already exporting or has been exported to this kahoot account!");
        }

        KahootQuizExportStatus kahootQuizExportStatus = KahootQuizExportStatus.builder()
                .quizUserId(QuizUserId.builder()
                        .quizId(id)
                        .kahootUserId(kahootAccount.getUuid())
                        .build())
                .quiz(quiz.get())
                .exportStatus(ExportStatus.EXPORTING)
                .build();
        kahootQuizExportStatusRepository.save(kahootQuizExportStatus);

        uploadNotUploadedImages(quiz.get(), kahootAccount.getAccessToken());

        RestClient restClient = new RestClient()
                .setBearerToken(kahootAccount.getAccessToken())
                .setContentType(MediaType.APPLICATION_JSON_VALUE);

        String eventName = quiz.get().getEventEntity().getTitle();
        String folderId = getOrCreateFolder(eventName).getId();

        exportQuiz(quiz.get(), folderId, restClient, kahootAccount);
    }

    private void exportQuiz(QuizEntity quiz, String folderId, RestClient restClient, KahootAccountEntity kahootAccount) {
        KahootCreateDraftRequestDTO kahootCreateDraftRequestDTO = new KahootCreateDraftRequestDTO(kahootQuizMapper.quizToKahootQuizDTO(quiz, folderId));
        KahootExportQuizResponseDTO response = createAndPublishQuiz(restClient, kahootCreateDraftRequestDTO);
        Optional<KahootQuizExportStatus> kahootQuizExportStatusOptional = kahootQuizExportStatusRepository.findByKahootUserIdAndQuizId(kahootAccount.getUuid(), quiz.getId());
        kahootQuizExportStatusOptional.ifPresent(kahootQuizExportStatus -> {
            kahootQuizExportStatus.setKahootQuizId(response.getKahootQuizId());
            kahootQuizExportStatus.setExportStatus(ExportStatus.EXPORTED);
            kahootQuizExportStatusRepository.save(kahootQuizExportStatus);
        });
    }

    @Override
    public ConcurrentMap<Long, String> uploadImages(AttachmentEntity[] attachments, String token) {
        ConcurrentMap<Long, String> concurrentMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_UPLOAD_IMAGE_THREAD);
        List<CompletableFuture<Void>> uploadFutures = new ArrayList<>();

        for (AttachmentEntity attachment : attachments) {
            CompletableFuture<Void> uploadFuture = CompletableFuture.supplyAsync(
                    () -> {
                        UploadImageToKahootResultDto res = ConvertBase64ToFile.convertAndUploadImage(attachment.getImageData(),
                                UPLOAD_IMAGE_URL, token);
                        if (res == null) {
                            throw new KahootException("Failed to upload image to Kahoot!");
                        }
                        concurrentMap.put(attachment.getId(), res.getUri());
                        return null;
                    }, executorService);
            uploadFuture.orTimeout(MAX_UPLOAD_IMAGE_TIMEOUT, TimeUnit.SECONDS);
            uploadFutures.add(uploadFuture);
        }
        CompletableFuture.allOf(uploadFutures.toArray(CompletableFuture[]::new)).join();

        executorService.shutdown();
        return concurrentMap;
    }

    @Override
    public void deleteKahootQuiz(long quizId) {
        KahootAccountEntity kahootAccount = getKahootAccount();
        if (kahootAccount == null) {
            return;
        }

        Optional<KahootQuizExportStatus> kahootQuizExportStatus =
                kahootQuizExportStatusRepository.findByKahootUserIdAndQuizId(kahootAccount.getUuid(), quizId);
        if (kahootQuizExportStatus.isPresent()) {
            String quizRemoveUrl = String.format(DELETE_QUIZ_URL, kahootQuizExportStatus.get().getKahootQuizId());
            try {
                new RestClient()
                        .setBearerToken(kahootAccount.getAccessToken())
                        .setContentType(MediaType.APPLICATION_JSON_VALUE)
                        .setUrl(quizRemoveUrl)
                        .setMethod("DELETE")
                        .call(JsonNode.class);
            } catch (HttpErrorStatusException ignored) {
                log.error("The Kahoot quiz has been deleted in the Kahoot app.");
            }
        }
    }

    public KahootQuizExportStatus getKahootQuizExportStatus(Long quizId) {
        Optional<KahootAccountEntity> kahootAccountEntityOptional = kahootAccountRepository.findFirstByOrderByUuid();
        return kahootAccountEntityOptional.
                flatMap(kahootAccountEntity -> kahootQuizExportStatusRepository.findByKahootUserIdAndQuizId(kahootAccountEntity.getUuid(), quizId))
                .orElse(null);
    }

    @Override
    public String exportEvent(long id) {
        KahootAccountEntity kahootAccount = validateKahootAccount();

        Optional<EventEntity> optionalEvent = eventRepository.findById(id);
        EventEntity event = optionalEvent.orElseThrow(() -> new RecordNotFoundException("The event with id " + id + " does not exist"));

        List<QuizEntity> quizzesToExport = event.getQuizEntity().stream()
                .filter(quizEntity -> !kahootQuizExportStatusRepository.existsByKahootUserIdAndQuizId(kahootAccount.getUuid(), quizEntity.getId()))
                .toList();

        quizzesToExport.forEach(quiz -> {
            KahootQuizExportStatus kahootQuizExportStatus = KahootQuizExportStatus.builder()
                    .quizUserId(
                            QuizUserId.builder()
                                    .quizId(quiz.getId())
                                    .kahootUserId(kahootAccount.getUuid())
                                    .build()
                    )
                    .exportStatus(ExportStatus.EXPORTING)
                    .build();
            kahootQuizExportStatusRepository.save(kahootQuizExportStatus);
        });

        String kahootAccessToken = kahootAccount.getAccessToken();
        quizzesToExport.forEach(quiz -> {
            uploadNotUploadedImages(quiz, kahootAccessToken);
        });

        RestClient restClient = new RestClient()
                .setBearerToken(kahootAccount.getAccessToken())
                .setContentType(MediaType.APPLICATION_JSON_VALUE);

        String eventName = event.getTitle();
        String folderId = getOrCreateFolder(eventName).getId();

        for (QuizEntity quiz : quizzesToExport) {
            exportQuiz(quiz, folderId, restClient, kahootAccount);
        }

        return folderId;
    }

    @Override
    public KahootAccountEntity validateKahootAccount() {
        KahootAccountEntity kahootAccount = getKahootAccount();
        if (kahootAccount != null) {
            return kahootAccount;
        }
        throw new KahootUnauthorizedException();
    }

    void uploadNotUploadedImages(QuizEntity quiz, String kahootAccessToken) {
        List<QuestionEntity> questionsHaveUnUploadedImage = quiz.getQuestions().stream()
                .filter(question -> question.getAttachment() != null && !question.getAttachment().getIsUploaded() && question.getAttachment().getImageData() != null)
                .toList();

        if (!questionsHaveUnUploadedImage.isEmpty()) {
            AttachmentEntity[] attachments = questionsHaveUnUploadedImage.stream()
                    .map(QuestionEntity::getAttachment)
                    .toArray(AttachmentEntity[]::new);

            ConcurrentMap<Long, String> concurrentMap = uploadImages(attachments, kahootAccessToken);
            questionsHaveUnUploadedImage.forEach(question -> {
                String imageUri = concurrentMap.get(question.getAttachment().getId());
                question.getAttachment().setKahootUrl(imageUri);
                question.getAttachment().setIsUploaded(true);
            });

            questionRepository.saveAll(questionsHaveUnUploadedImage);
        }
    }
}

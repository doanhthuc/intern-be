package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.dto.auth.AuthenticationRequestDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootExportQuizResponseDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootFolderDTO;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserStatusResponseDto;
import com.mgmtp.easyquizy.mapper.KahootAccountMapper;
import com.mgmtp.easyquizy.model.kahoot.KahootAccountEntity;
import com.mgmtp.easyquizy.service.KahootService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Tag(name = "Kahoot")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/kahoot")
public class KahootController {
    private final KahootService kahootService;

    private final KahootAccountMapper kahootAccountMapper;

    @Operation(summary = "Authenticate user to get Kahoot access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connect Kahoot Success",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401", description = "Authentication fail"),
            @ApiResponse(responseCode = "400", description = "Invalid username or password")
    })
    @PostMapping("/auth")
    public KahootUserStatusResponseDto authenticateKahoot(@Valid @RequestBody AuthenticationRequestDTO authenticationRequest)
            throws IllegalStateException {
        return kahootService.authenticate(authenticationRequest);
    }

    @Operation(summary = "Get status Kahoot account")
    @ApiResponse(responseCode = "200", description = "Connect Kahoot Success or Fail",
            content = {@Content(mediaType = "application/json")})
    @GetMapping("/status")
    public KahootUserStatusResponseDto getStatus() {
        KahootAccountEntity kahootAccountEntity = kahootService.getKahootAccount();
        if (kahootAccountEntity != null) {
            KahootUserStatusResponseDto kahootUserStatusResponseDto =
                    kahootAccountMapper.kahootAccountEntityToUserStatusDto(kahootAccountEntity);
            kahootUserStatusResponseDto.setIsConnected(true);
            return kahootUserStatusResponseDto;
        }
        return KahootUserStatusResponseDto.getDisconnectDto();
    }

    @Operation(summary = "Log out Kahoot account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Log out successfully",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401", description = "Authentication fail")
    })
    @PostMapping("/logout")
    public KahootUserStatusResponseDto logOutKahootAccount() {
        return kahootService.logout();
    }

    @Operation(summary = "Export a quiz", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Export a quiz successfully",
                    content = {@Content(mediaType = "application/json")}
            ),
            @ApiResponse(responseCode = "401", description = "Kahoot authentication fail")
    })
    @GetMapping("/export/quiz/{id}")
    public KahootExportQuizResponseDTO exportQuiz(@Parameter(description = "The id of the quiz to export") @PathVariable long id) {
        return kahootService.exportQuiz(id);
    }

    @Operation(summary = "Create a new folder on Kahoot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create folder success",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401", description = "Authentication fail"),
            @ApiResponse(responseCode = "400", description = "Invalid folder name")
    })
    @PostMapping("/folders")
    public ResponseEntity<KahootFolderDTO> createFolder(@RequestBody Map<String, String> requestBody) throws IllegalStateException {
        String folderName = requestBody.get("name");
        KahootFolderDTO folder = kahootService.getOrCreateFolder(folderName);
        return ResponseEntity.ok(folder);
    }
}

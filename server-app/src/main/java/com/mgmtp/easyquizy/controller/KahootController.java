package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.dto.kahoot.KahootUserStatusResponseDto;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.mapper.KahootAccountMapper;
import com.mgmtp.easyquizy.model.auth.AuthenticationRequest;
import com.mgmtp.easyquizy.model.kahoot.KahootAccountEntity;
import com.mgmtp.easyquizy.service.KahootService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public KahootUserStatusResponseDto authenticateKahoot(@Valid @RequestBody AuthenticationRequest authenticationRequest)
            throws InvalidFieldsException {
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
}

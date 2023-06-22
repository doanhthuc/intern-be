package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.model.auth.AuthenticationRequest;
import com.mgmtp.easyquizy.model.auth.AuthenticationResponse;
import com.mgmtp.easyquizy.model.auth.RegisterRequest;
import com.mgmtp.easyquizy.service.impl.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @Operation(summary = "Create user and user role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create user Success",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Username already exist")
    })
    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody RegisterRequest registerRequest) throws RuntimeException{
        return authenticationService.register(registerRequest);
    }

    @Operation(summary = "Authenticate user to get access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication Success",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Authentication fail")
    })
    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) throws RuntimeException {
        return authenticationService.authenticate(request);
    }
}

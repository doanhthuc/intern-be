package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.dto.user.UserDTO;
import com.mgmtp.easyquizy.mapper.UserMapper;
import com.mgmtp.easyquizy.model.auth.ChangePasswordRequest;
import com.mgmtp.easyquizy.model.user.UserEntity;
import com.mgmtp.easyquizy.service.UserServiceImpl;
import com.mgmtp.easyquizy.validator.StrongPasswordValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "User")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private StrongPasswordValidator strongPasswordValidator;
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(strongPasswordValidator);
    }

    @Operation(summary = "Get user information by JWT", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserEntity user) {
                return ResponseEntity.ok(userMapper.entityToUserDTO(user));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "Change user's password", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @PutMapping("/me/password")
    public ResponseEntity<UserDTO> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserEntity user) {
                userService.changePassword(user, request);
                return ResponseEntity.ok(userMapper.entityToUserDTO(user));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

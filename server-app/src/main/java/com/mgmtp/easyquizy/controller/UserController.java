package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.dto.auth.ChangePasswordRequestDTO;
import com.mgmtp.easyquizy.dto.user.UserDTO;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.mapper.UserMapper;
import com.mgmtp.easyquizy.model.user.UserEntity;
import com.mgmtp.easyquizy.service.UserServiceImpl;
import com.mgmtp.easyquizy.validator.StrongPasswordValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Tag(name = "User")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    UserServiceImpl userService;

    private final StrongPasswordValidator strongPasswordValidator;

    @InitBinder("changePasswordRequest")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(strongPasswordValidator);
    }

    @Value("${easy-quizy.api.default-page-size}")
    private int defaultPageSize;

    @Operation(summary = "Get all users with paging, filtering (if needed)", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found users", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @GetMapping
    public Page<UserDTO> getAllUsers(
            @Parameter(description = "The offset of the first result to return")
            @Min(value = 0, message = "Offset must be greater than or equal to 0")
            @RequestParam(required = false, defaultValue = "0") int offset,
            @Parameter(description = "The maximum number of results to return")
            @Min(value = 1, message = "Limit must be greater than or equal to 1")
            @RequestParam(required = false) Integer limit,
            @Parameter(description = "The keyword to search for")
            @RequestParam(required = false) String keyword) {
        if (limit == null) {
            limit = defaultPageSize;
        }
        return userService.getAllUsers(keyword, offset, limit);
    }

    @Operation(summary = "Get user by id", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found users", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @GetMapping("/{id}")
    public UserDTO getUserById(@Parameter(description = "The ID of the user to get") @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Update an existing user", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @PutMapping
    public UserDTO updateUser(@Valid @RequestBody UserDTO userDTO) throws RecordNotFoundException, InvalidFieldsException {
        return userService.updateUser(userDTO);
    }

    @Operation(summary = "Delete an user by ID", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "The ID of the user to delete", example = "1")
            @PathVariable Long id) throws RecordNotFoundException {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Reset password by User ID", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @PutMapping("/reset-password/{id}")
    public ResponseEntity<String> resetPassword(
            @Parameter(description = "The ID of the user to reset password", example = "1")
            @PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok(userService.resetPassword(id));
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
    public ResponseEntity<UserDTO> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
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

package com.mgmtp.easyquizy.dto.kahoot;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class KahootAuthenticationRequestDTO {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 149, message = "The password must have between 6 and 149 characters")
    private String password;
}

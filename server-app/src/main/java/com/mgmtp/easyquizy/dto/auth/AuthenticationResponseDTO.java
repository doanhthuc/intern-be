package com.mgmtp.easyquizy.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgmtp.easyquizy.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private UserDTO user;
}

package com.mgmtp.easyquizy.model.auth;

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
public class AuthenticationResponse {
    @JsonProperty("access_token")
    private String accessToken;
    private UserDTO user;
}

package com.mgmtp.easyquizy.dto.kahoot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KahootUserInfo {
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("email")
    private String email;
    @JsonProperty("username")
    private String username;
}

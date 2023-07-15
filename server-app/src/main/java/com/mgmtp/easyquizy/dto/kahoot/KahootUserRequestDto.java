package com.mgmtp.easyquizy.dto.kahoot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KahootUserRequestDto {
    private String username;
    private String password;
    private String grant_type;
}

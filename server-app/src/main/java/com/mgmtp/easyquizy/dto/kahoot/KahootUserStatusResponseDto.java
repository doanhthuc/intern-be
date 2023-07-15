package com.mgmtp.easyquizy.dto.kahoot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KahootUserStatusResponseDto {
    private Boolean isConnected;
    private String email;
    private String username;
    public static KahootUserStatusResponseDto getDisconnectDto(){
        return KahootUserStatusResponseDto.builder().isConnected(false).build();
    }
}

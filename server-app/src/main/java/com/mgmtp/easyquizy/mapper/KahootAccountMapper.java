package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.kahoot.KahootUserResponseDto;
import com.mgmtp.easyquizy.dto.kahoot.KahootUserStatusResponseDto;
import com.mgmtp.easyquizy.model.kahoot.KahootAccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KahootAccountMapper {
    @Mapping(source = "kahootUserInfo.uuid", target = "uuid")
    @Mapping(source = "kahootUserInfo.username", target = "username")
    @Mapping(source = "kahootUserInfo.email", target = "email")
    KahootAccountEntity userResponseDtoToAccountEntity(KahootUserResponseDto kahootUserResponseDto);

    KahootUserStatusResponseDto kahootAccountEntityToUserStatusDto(KahootAccountEntity kahootAccountEntity);
}

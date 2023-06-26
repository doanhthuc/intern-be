package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.UserDTO;
import com.mgmtp.easyquizy.model.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "roles", target = "roles")
    UserDTO entityToUserDTO(UserEntity userEntity);
}

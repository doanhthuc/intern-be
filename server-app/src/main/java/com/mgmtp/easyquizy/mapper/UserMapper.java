package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.UserDTO;
import com.mgmtp.easyquizy.model.role.RoleEntity;
import com.mgmtp.easyquizy.model.role.RoleName;
import com.mgmtp.easyquizy.model.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "roles", target = "roles", qualifiedByName = "roleEntityToStringList")
    UserDTO entityToUserDTO(UserEntity userEntity);

    @Named("roleEntityToStringList")
    default List<RoleName> roleEntityToStringList(List<RoleEntity> roles) {
        return roles.stream()
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toList());
    }
}

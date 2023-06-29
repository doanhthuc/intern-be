package com.mgmtp.easyquizy.dto;

import com.mgmtp.easyquizy.model.role.RoleName;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String avatar;
    private List<RoleName> roles;
}

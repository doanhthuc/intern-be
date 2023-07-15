package com.mgmtp.easyquizy.dto.role;

import com.mgmtp.easyquizy.model.role.RoleName;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Long id;
    private RoleName roleName;
}

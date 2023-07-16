package com.mgmtp.easyquizy.dto.user;

import com.mgmtp.easyquizy.model.role.RoleName;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;

    @NotEmpty(message = "Username should not be empty")
    @Size(max = 30, message = "Username should not be over 30 characters")
    private String username;
    @NotEmpty(message = "User name should not be empty")
    @Size(max = 30, message = "Name should not be over 30 characters")
    private String name;
    private String avatar;

    @Size(min = 1, message = "At least pick one role")
    @NotNull
    private List<RoleName> roles;
}

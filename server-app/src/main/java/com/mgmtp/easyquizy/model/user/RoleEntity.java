package com.mgmtp.easyquizy.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgmtp.easyquizy.model.UserEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "roles")
@Builder
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    @ManyToMany(mappedBy = "roleEntity")
    @JsonIgnore
    private List<UserEntity> users;
}

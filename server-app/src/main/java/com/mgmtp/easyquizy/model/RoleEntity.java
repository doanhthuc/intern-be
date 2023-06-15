package com.mgmtp.easyquizy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    public enum RoleName {
        IMAGE, CODE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    @ManyToMany(mappedBy = "roleEntity")
    @JsonIgnore
    private List<UserEntity> users;
}

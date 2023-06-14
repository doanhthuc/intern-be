package com.mgmtp.easyquizy.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity(name = "users")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;

    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "username")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<RoleEntity> roleEntity;
}

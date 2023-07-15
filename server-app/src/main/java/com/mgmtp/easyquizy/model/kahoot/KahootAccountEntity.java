package com.mgmtp.easyquizy.model.kahoot;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "kahoot_account")
@Builder
public class KahootAccountEntity {
    @Id
    private String uuid;

    @Column(name = "access_token", columnDefinition = "TEXT", nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(name = "expire_time", nullable = false)
    private Long expireTime;
}

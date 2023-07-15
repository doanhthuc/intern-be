package com.mgmtp.easyquizy.repository;

import com.mgmtp.easyquizy.model.kahoot.KahootAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KahootAccountRepository extends JpaRepository<KahootAccountEntity, String> {
    Optional<KahootAccountEntity> findFirstByOrderByUuid();
}

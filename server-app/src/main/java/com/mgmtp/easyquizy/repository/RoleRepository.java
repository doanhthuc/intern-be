package com.mgmtp.easyquizy.repository;

import com.mgmtp.easyquizy.model.role.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    List<RoleEntity> findAllByIdIsIn(List<Long> ids);
}

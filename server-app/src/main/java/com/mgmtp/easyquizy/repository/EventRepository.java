package com.mgmtp.easyquizy.repository;

import com.mgmtp.easyquizy.model.event.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
}

package com.mgmtp.easyquizy.repository;

import com.mgmtp.easyquizy.model.attachment.AttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {
}

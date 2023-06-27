package com.mgmtp.easyquizy.repository;

import com.mgmtp.easyquizy.model.answer.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
    void deleteByQuestionId(Long id);
}

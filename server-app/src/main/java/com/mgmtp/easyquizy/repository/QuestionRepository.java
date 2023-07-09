package com.mgmtp.easyquizy.repository;

import com.mgmtp.easyquizy.model.question.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long>, JpaSpecificationExecutor<QuestionEntity> {
    boolean existsByCategoryId(Long id);

    List<QuestionEntity> findByCategoryIdIn(Set<Long> categoryIds);
}

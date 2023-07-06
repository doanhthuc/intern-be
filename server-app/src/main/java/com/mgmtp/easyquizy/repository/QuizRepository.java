package com.mgmtp.easyquizy.repository;

import com.mgmtp.easyquizy.model.quiz.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface QuizRepository extends JpaRepository<QuizEntity, Long>, JpaSpecificationExecutor<QuizEntity> {
    @Modifying
    @Query(value = "DELETE FROM questions_quizzes WHERE quiz_id = :quizId", nativeQuery = true)
    void removeQuestionFromQuiz(@Param("quizId") Long quizId);
}

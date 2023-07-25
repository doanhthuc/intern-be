package com.mgmtp.easyquizy.repository;

import com.mgmtp.easyquizy.model.kahoot.KahootQuizExportStatus;
import com.mgmtp.easyquizy.model.kahoot.QuizUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface KahootQuizExportStatusRepository extends JpaRepository<KahootQuizExportStatus, QuizUserId> {
    @Query(" FROM kahoot_quiz_export_status kqes " +
            "WHERE kqes.quizUserId.kahootUserId = :kahootUserId AND kqes.quizUserId.quizId = :quizId")
    Optional<KahootQuizExportStatus> findByKahootUserIdAndQuizId(
            @Param("kahootUserId") String kahootUserId, @Param("quizId") Long quizId);

    @Query("SELECT CASE " +
            "WHEN COUNT(kqes) > 0 " +
            "THEN true " +
            "ELSE false " +
            "END " +
            "FROM kahoot_quiz_export_status kqes " +
            "WHERE kqes.quizUserId.kahootUserId = :kahootUserId AND kqes.quizUserId.quizId = :quizId")
    boolean existsByKahootUserIdAndQuizId(String kahootUserId, Long quizId);
}

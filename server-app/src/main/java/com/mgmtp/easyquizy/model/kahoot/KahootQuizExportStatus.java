package com.mgmtp.easyquizy.model.kahoot;

import com.mgmtp.easyquizy.model.quiz.QuizEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "kahoot_quiz_export_status")
@Builder
public class KahootQuizExportStatus {
    @EmbeddedId
    private QuizUserId quizUserId;

    @Column
    private String kahootQuizId;

    @Enumerated(EnumType.STRING)
    private ExportStatus exportStatus;

    @ManyToOne
    @JoinColumn(name = "quiz_id", referencedColumnName = "id", insertable = false, updatable = false)
    private QuizEntity quiz;
}

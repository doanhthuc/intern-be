package com.mgmtp.easyquizy.model.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgmtp.easyquizy.model.AnswerEntity;
import com.mgmtp.easyquizy.model.CategoryEntity;
import com.mgmtp.easyquizy.model.QuizEntity;
import com.mgmtp.easyquizy.model.attachment.AttachmentEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "questions")
@Builder
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(name = "time_limit", nullable = false)
    private Integer timeLimit;

    @OneToMany(mappedBy = "questionEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnswerEntity> answerEntities;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_category_question"), referencedColumnName = "id")
    private CategoryEntity categoryEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", foreignKey = @ForeignKey(name = "FK_attachment_question"), referencedColumnName = "id")
    private AttachmentEntity attachmentEntity;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "questions_quizzes",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @JsonIgnore
    private List<QuizEntity> quizEntities;
}

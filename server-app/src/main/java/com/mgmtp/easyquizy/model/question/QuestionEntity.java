package com.mgmtp.easyquizy.model.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgmtp.easyquizy.model.answer.AnswerEntity;
import com.mgmtp.easyquizy.model.category.CategoryEntity;
import com.mgmtp.easyquizy.model.quiz.QuizEntity;
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

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnswerEntity> answers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_category_question"), referencedColumnName = "id")
    private CategoryEntity category;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "attachment_id", foreignKey = @ForeignKey(name = "FK_attachment_question"), referencedColumnName = "id")
    private AttachmentEntity attachment;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "questions_quizzes",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @JsonIgnore
    private List<QuizEntity> quizzes;

    public void setAnswers(List<AnswerEntity> answers) {
        answers.forEach(answer -> {
            answer.setQuestion(this);
        });
        this.answers = answers;
    }
}

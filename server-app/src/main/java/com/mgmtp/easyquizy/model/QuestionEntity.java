package com.mgmtp.easyquizy.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "questions")
public class QuestionEntity {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(name = "time_limit", nullable = false)
    private Integer timeLimit;

    @OneToMany(mappedBy = "questionEntity", fetch = FetchType.LAZY)
    private List<AnswerEntity> answerEntities;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_category_question"), referencedColumnName = "id")
    private CategoryEntity categoryEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", foreignKey = @ForeignKey(name = "FK_attachment_question"), referencedColumnName = "id")
    private AttachmentEntity attachmentEntity;
}

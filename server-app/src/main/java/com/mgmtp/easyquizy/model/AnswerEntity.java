package com.mgmtp.easyquizy.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "answers")
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "is_correct", columnDefinition = "BIT", nullable = false)
    private Boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", foreignKey = @ForeignKey(name = "FK_answer_question"), referencedColumnName = "id")
    private QuestionEntity questionEntity;


}

package com.mgmtp.easyquizy.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "quizzes")
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizzes_id", foreignKey = @ForeignKey(name = "fk_quizzes_events"), referencedColumnName = "id")
    private EventEntity eventEntity;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "quizEntities")
    private List<QuestionEntity> questionEntities;

}

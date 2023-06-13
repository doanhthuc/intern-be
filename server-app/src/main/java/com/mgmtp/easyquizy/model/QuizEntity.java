package com.mgmtp.easyquizy.model;

import lombok.*;

import javax.persistence.*;

@Entity(name = "quizzes")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizzes_id", foreignKey = @ForeignKey(name = "fk_quizzes_events"), referencedColumnName = "id")
    private EventEntity eventEntity;
}

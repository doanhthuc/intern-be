package com.mgmtp.easyquizy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "categories")
@Builder
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @OneToMany(mappedBy = "categoryEntity", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuestionEntity> questionEntities;
}

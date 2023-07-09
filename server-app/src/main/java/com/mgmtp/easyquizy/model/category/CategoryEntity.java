package com.mgmtp.easyquizy.model.category;

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

    @Column(name = "name", length = 20, nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuestionEntity> questions;
}

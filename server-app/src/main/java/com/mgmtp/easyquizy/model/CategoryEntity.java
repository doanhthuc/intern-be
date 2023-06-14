package com.mgmtp.easyquizy.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "categories")
public class CategoryEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @OneToMany(mappedBy = "categoryEntity", fetch = FetchType.LAZY)
    private List<QuestionEntity> questionEntities;

}

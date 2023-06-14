package com.mgmtp.easyquizy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "attachments")
@Builder
public class AttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Type(type = "text")
    private String content;

    @Column(length = 20, nullable = false)
    private String Type;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "attachmentEntity")
    @JsonIgnore
    private QuestionEntity questionEntity;

}

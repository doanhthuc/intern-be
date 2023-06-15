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
    public enum TypeAttach {
        IMAGE, CODE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Type(type = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    private TypeAttach typeAttach;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "attachmentEntity")
    @JsonIgnore
    private QuestionEntity questionEntity;
}

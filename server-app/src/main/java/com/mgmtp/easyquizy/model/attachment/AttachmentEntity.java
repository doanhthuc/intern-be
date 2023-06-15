package com.mgmtp.easyquizy.model.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
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

    @Enumerated(EnumType.STRING)
    private AttachType attachType;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "attachmentEntity")
    @JsonIgnore
    private QuestionEntity questionEntity;
}

package com.mgmtp.easyquizy.model.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
    @NotNull
    private String imageData;

    @Column(columnDefinition = "TEXT")
    private String sourceCode;

    @Column
    private String kahootUrl;

    @Column
    private Boolean isUploaded;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "attachment")
    @JsonIgnore
    private QuestionEntity question;
}

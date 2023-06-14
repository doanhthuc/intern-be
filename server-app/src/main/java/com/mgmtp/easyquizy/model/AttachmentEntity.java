package com.mgmtp.easyquizy.model;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "attachments")
public class AttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 20, nullable = false)
    private String Type;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "attachmentEntity")
    private QuestionEntity questionEntity;
}

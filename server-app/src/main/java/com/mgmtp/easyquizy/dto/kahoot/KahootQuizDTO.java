package com.mgmtp.easyquizy.dto.kahoot;

import lombok.*;

import java.util.List;

@Data
@Builder
public class KahootQuizDTO {
    private String folderId;
    private List<KahootQuestionDTO> questions;
    private String title;
    @Builder.Default
    private String quizType = "quiz";
    @Builder.Default
    private String language = "English";
}

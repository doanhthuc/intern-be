package com.mgmtp.easyquizy.dto.question;

import com.mgmtp.easyquizy.model.question.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionListViewDTO {
    private Long id;
    private String title;
    private Difficulty difficulty;
    private String categoryName;
    private Integer timeLimit;
}

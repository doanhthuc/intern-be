package com.mgmtp.easyquizy.dto;

import com.mgmtp.easyquizy.model.question.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionListViewDTO {
    private Long id;
    private String title;
    private Difficulty difficulty;
    private String categoryName;
}

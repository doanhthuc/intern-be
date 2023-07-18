package com.mgmtp.easyquizy.dto.kahoot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KahootQuestionDTO {
    private String question;
    private String image;
    private List<KahootAnswerDTO> choices;
    private Integer time;
}

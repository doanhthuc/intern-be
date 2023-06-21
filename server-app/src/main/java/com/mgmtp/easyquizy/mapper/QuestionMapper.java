package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.QuestionListViewDTO;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {
    public QuestionListViewDTO questionToQuestionListViewDTO(QuestionEntity question) {
        return new QuestionListViewDTO(question.getId(), question.getTitle(), question.getDifficulty(),
                question.getCategoryEntity().getName());
    }
}

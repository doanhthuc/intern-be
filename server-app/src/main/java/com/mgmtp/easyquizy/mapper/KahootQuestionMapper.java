package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.kahoot.KahootQuestionDTO;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = KahootAnswerMapper.class)
public interface KahootQuestionMapper {
    @Mapping(target = "question", source = "questionEntity.title")
    @Mapping(target = "time", expression = "java(questionEntity.getTimeLimit() * 1000)")
    @Mapping(target = "choices", source = "questionEntity.answers")
    KahootQuestionDTO questionToKahootQuestionDTO(QuestionEntity questionEntity);
}


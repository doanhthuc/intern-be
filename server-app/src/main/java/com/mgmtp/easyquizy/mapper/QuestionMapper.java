package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.QuestionDTO;
import com.mgmtp.easyquizy.dto.QuestionListViewDTO;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    QuestionDTO questionToQuestionDTO(QuestionEntity question);
    QuestionEntity questionDTOToQuestion(QuestionDTO questionDTO);
    @Mapping(source = "question.category.name", target = "categoryName")
    QuestionListViewDTO questionToQuestionListViewDTO(QuestionEntity question);
}

package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.kahoot.KahootAnswerDTO;
import com.mgmtp.easyquizy.model.answer.AnswerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KahootAnswerMapper {
    @Mapping(target = "answer", source = "answerEntity.text")
    @Mapping(target = "correct", source = "answerEntity.isCorrect")
    KahootAnswerDTO toKahootAnswerDTO(AnswerEntity answerEntity);
}


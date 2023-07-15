package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.answer.AnswerDTO;
import com.mgmtp.easyquizy.model.answer.AnswerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    AnswerEntity answerDTOtoAnswer(AnswerDTO answerDTO);
}

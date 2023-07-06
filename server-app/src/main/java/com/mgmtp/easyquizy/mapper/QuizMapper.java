package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.QuizDTO;
import com.mgmtp.easyquizy.dto.QuizDtoDetail;
import com.mgmtp.easyquizy.model.quiz.QuizEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    @Mapping(target = "event", source = "eventEntity")
    @Mapping(target = "totalTime", ignore = true)
    QuizDtoDetail quizEntityToQuizDtoDetail(QuizEntity quizEntity);

    @Mapping(target = "event", source = "eventEntity")
    @Mapping(target = "totalTime", ignore = true)
    QuizDTO quizEntityToQuizDTO(QuizEntity quizEntity);

    @Mapping(target = "eventEntity", source = "event")
    QuizEntity quizDtoDetailToQuizEntity(QuizDtoDetail quizDtoDetail);
}

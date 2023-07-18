package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.kahoot.KahootQuizDTO;
import com.mgmtp.easyquizy.model.quiz.QuizEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring", uses = KahootQuestionMapper.class)
public interface KahootQuizMapper {
    @Mapping(target = "folderId", source = "folderId")
    @Mapping(target = "title", source = "quizEntity.title")
    @Mapping(target = "questions", source = "quizEntity.questions")
    KahootQuizDTO quizToKahootQuizDTO(QuizEntity quizEntity, String folderId);
}


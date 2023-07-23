package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.question.QuestionListViewDTO;
import com.mgmtp.easyquizy.dto.quiz.QuizDTO;
import com.mgmtp.easyquizy.dto.quiz.QuizDtoDetail;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import com.mgmtp.easyquizy.model.quiz.QuizEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    @Mapping(target = "eventId", source = "eventEntity.id")
    @Mapping(target = "totalTime", ignore = true)
    QuizDTO quizEntityToQuizDTO(QuizEntity quizEntity);

    @Mapping(target = "event", source = "eventEntity")
    @Mapping(target = "totalTime", ignore = true)
    @Mapping(target = "questions", source = "questions", qualifiedByName = "customMappingQuestion")
    QuizDtoDetail quizEntityToQuizDtoDetail(QuizEntity quizEntity);

    @Named("customMappingQuestion")
    default List<QuestionListViewDTO> customMappingQuestion(List<QuestionEntity> questionEntities) {
        if (questionEntities == null) {
            return Collections.emptyList();
        }
        return questionEntities.stream()
                .map(questionEntity -> QuestionListViewDTO.builder()
                        .id(questionEntity.getId())
                        .title(questionEntity.getTitle())
                        .timeLimit(questionEntity.getTimeLimit())
                        .categoryName(questionEntity.getCategory().getName())
                        .difficulty(questionEntity.getDifficulty())
                        .build())
                .toList();
    }
}

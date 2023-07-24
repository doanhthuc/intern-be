package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.question.QuestionListViewDTO;
import com.mgmtp.easyquizy.dto.quiz.QuizDTO;
import com.mgmtp.easyquizy.dto.quiz.QuizDtoDetail;
import com.mgmtp.easyquizy.model.kahoot.ExportStatus;
import com.mgmtp.easyquizy.model.kahoot.KahootQuizExportStatus;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import com.mgmtp.easyquizy.model.quiz.QuizEntity;
import com.mgmtp.easyquizy.service.KahootService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class QuizMapper {
    @Autowired
    protected KahootService kahootService;

    @Mapping(target = "eventId", source = "eventEntity.id")
    @Mapping(target = "totalTime", ignore = true)
    public abstract QuizDTO quizEntityToQuizDTO(QuizEntity quizEntity);

    @Mapping(target = "event", source = "eventEntity")
    @Mapping(target = "totalTime", ignore = true)
    @Mapping(target = "questions", source = "questions", qualifiedByName = "customMappingQuestion")
    public abstract QuizDtoDetail quizEntityToQuizDtoDetail(QuizEntity quizEntity);

    @Named("customMappingQuestion")
    List<QuestionListViewDTO> customMappingQuestion(List<QuestionEntity> questionEntities) {
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

    @Mapping(target = "eventEntity", source = "event")
    public abstract QuizEntity quizDtoDetailToQuizEntity(QuizDtoDetail quizDtoDetail);

    @AfterMapping
    protected void setExportStatusAndKahootQuizId(QuizEntity quizEntity, @MappingTarget QuizDTO quizDTO) {
        KahootQuizExportStatus kahootQuizExportStatus = kahootService.getKahootQuizExportStatus(quizDTO.getId());
        if (kahootQuizExportStatus != null) {
            quizDTO.setExportStatus(kahootQuizExportStatus.getExportStatus());
            quizDTO.setKahootQuizId(kahootQuizExportStatus.getKahootQuizId());
        } else {
            quizDTO.setExportStatus(ExportStatus.NOT_EXPORTED);
        }
    }
}

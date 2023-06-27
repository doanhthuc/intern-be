package com.mgmtp.easyquizy.dto;

import com.mgmtp.easyquizy.model.quiz.QuizEntity;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class EventDTO {
    @NotNull
    private Long id;

    @NotEmpty
    private String title;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotEmpty
    private String location;

    private List<QuizEntity> quizEntity;
}

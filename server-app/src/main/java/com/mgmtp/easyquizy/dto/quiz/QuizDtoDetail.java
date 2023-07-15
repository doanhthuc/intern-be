package com.mgmtp.easyquizy.dto.quiz;

import com.mgmtp.easyquizy.dto.event.EventDTO;
import com.mgmtp.easyquizy.dto.question.QuestionDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class QuizDtoDetail {
    private Long id;

    @NotEmpty
    @Size(max = 255, message = "Title must not be exceed 255 characters")
    private String title;

    private EventDTO event;

    private List<QuestionDTO> questions;

    private Integer totalTime;
}

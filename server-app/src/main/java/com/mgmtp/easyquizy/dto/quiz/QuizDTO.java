package com.mgmtp.easyquizy.dto.quiz;

import com.mgmtp.easyquizy.model.kahoot.ExportStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class QuizDTO {
    private Long id;

    @NotEmpty
    @Size(max = 255, message = "Title must not be exceed 255 characters")
    private String title;

    @NotNull(message = "This field is required")
    @Positive(message = "Event's ID must be greater than zero")
    private Long eventId;

    @NotNull(message = "This field is required")
    @Size(min = 1, message = "The list must contain at least one question")
    private List<Long> questionIds;

    private Integer totalTime;

    private ExportStatus exportStatus;

    private String kahootQuizId;
}

package com.mgmtp.easyquizy.dto;

import com.mgmtp.easyquizy.model.question.Difficulty;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class QuestionDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not be exceed 255 characters")
    private String title;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @NotNull(message = "Please enter a valid number in this field")
    @Positive(message = "Time limit must be greater than zero")
    private Integer timeLimit;

    private List<AnswerDTO> answers;

    private CategoryDTO category;

    private AttachmentDTO attachment;
}

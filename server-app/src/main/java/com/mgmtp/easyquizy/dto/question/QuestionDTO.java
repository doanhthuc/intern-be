package com.mgmtp.easyquizy.dto.question;

import com.mgmtp.easyquizy.dto.answer.AnswerDTO;
import com.mgmtp.easyquizy.dto.attachment.AttachmentDTO;
import com.mgmtp.easyquizy.dto.category.CategoryDTO;
import com.mgmtp.easyquizy.model.question.Difficulty;
import lombok.*;

import javax.validation.Valid;
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

    @NotBlank(message = "This field is required")
    @Size(max = 255, message = "Title must not be exceed 255 characters")
    private String title;

    @NotNull(message = "This field is required")
    private Difficulty difficulty;

    @NotNull(message = "Please enter a valid number in this field")
    @Positive(message = "Time limit must be greater than zero")
    private Integer timeLimit;

    @Valid
    private List<AnswerDTO> answers;

    @NotNull
    @Valid
    private CategoryDTO category;

    @Valid
    private AttachmentDTO attachment;
}

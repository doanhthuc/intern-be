package com.mgmtp.easyquizy.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AnswerDTO {
    private Long id;

    @NotBlank(message = "This field is required")
    @Size(max = 120, message = "Answer must not exceed 120 characters")
    private String text;

    @NotNull(message = "This field is required")
    private Boolean isCorrect;
}

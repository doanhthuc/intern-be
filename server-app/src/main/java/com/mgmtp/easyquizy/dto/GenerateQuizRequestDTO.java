package com.mgmtp.easyquizy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Map;

@Data
@NoArgsConstructor
public class GenerateQuizRequestDTO {
    @Schema(description = "The total time for the quiz in seconds")
    @NotNull(message = "This field is required")
    @Positive(message = "Total time must be a positive number")
    @Max(value = 3600, message = "Total time must not exceed 3600 seconds")
    int totalTime;

    @NotNull(message = "This field is required")
    @Size(min = 1, message = "At least one category must be selected")
    Map<Long, @Positive(message = "Category percentage must be a positive number") Double> categoryPercentages;
}

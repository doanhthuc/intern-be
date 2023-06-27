package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.dto.QuestionListViewDTO;
import com.mgmtp.easyquizy.model.question.Difficulty;
import com.mgmtp.easyquizy.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

@Tag(name = "Question")
@RequiredArgsConstructor
@RestController
@RequestMapping("/questions")
@Validated
public class QuestionController {
    private final QuestionService questionService;

    @Value("${easy-quizy.api.questions.default-page-size}")
    private int defaultPageSize;

    @Operation(summary = "Get all questions with paging, filtering (if needed)", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found questions",
            content = {@Content(mediaType = "application/json")})
    })
    @GetMapping
    public Page<QuestionListViewDTO> getAllQuestions(
            @Parameter(description = "The offset of the first result to return")
            @Min(value = 0, message = "Offset must be greater than or equal to 0")
            @RequestParam(required = false, defaultValue = "0") int offset,
            @Parameter(description = "The maximum number of results to return")
            @Min(value = 1, message = "Limit must be greater than or equal to 1")
            @RequestParam(required = false) Integer limit,
            @Parameter(description = "The keyword to search for")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "The difficulty level to filter by")
            @RequestParam(required = false) Difficulty difficulty,
            @Parameter(description = "The ID of the category to filter by")
            @RequestParam(required = false) Integer categoryId){
        if (limit == null) {
            limit = defaultPageSize;
        }
        return questionService.getAllQuestions(keyword, difficulty, categoryId, offset, limit);
    }
}

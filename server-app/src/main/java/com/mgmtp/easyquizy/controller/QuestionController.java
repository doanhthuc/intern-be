package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.dto.QuestionListViewDTO;
import com.mgmtp.easyquizy.model.question.Difficulty;
import com.mgmtp.easyquizy.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;

    @Value("${easy-quizy.api.questions.default-page-size}")
    private int defaultPageSize;

    @Operation(summary = "Get all questions with paging, filtering (if needed)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found questions",
            content = {@Content(mediaType = "application/json")})
    })
    @GetMapping
    public Page<QuestionListViewDTO> getAllQuestions(
            @Parameter(description = "The offset of the first result to return")
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @Parameter(description = "The maximum number of results to return")
            @RequestParam(name = "limit", required = false) Integer limit,
            @Parameter(description = "The keyword to search for")
            @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "The difficulty level to filter by")
            @RequestParam(name = "difficulty", required = false) Difficulty difficulty,
            @Parameter(description = "The ID of the category to filter by")
            @RequestParam(name = "categoryId", required = false) Integer categoryId) {
        if (limit == null) {
            limit = defaultPageSize;
        }
        return questionService.getAllQuestions(keyword, difficulty, categoryId, offset, limit);
    }
}

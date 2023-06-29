package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.dto.QuestionDTO;
import com.mgmtp.easyquizy.dto.QuestionListViewDTO;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.model.question.Difficulty;
import com.mgmtp.easyquizy.service.QuestionService;
import com.mgmtp.easyquizy.validator.QuestionDTOValidator;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Tag(name = "Question")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/questions")
@Validated
public class QuestionController {
    private final QuestionService questionService;
    private final QuestionDTOValidator questionDTOValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(questionDTOValidator);
    }

    @Value("${easy-quizy.api.default-page-size}")
    private int defaultPageSize;

    @Operation(summary = "Get all questions with paging, filtering (if needed)", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found questions", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
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

    @Operation(summary = "Get a question by its id", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all questions", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    @GetMapping("/{id}")
    public QuestionDTO getQuestionById(@Parameter(description = "The ID of the question to get") @PathVariable Long id) throws RecordNotFoundException {
        return questionService.getQuestionById(id);
    }

    @Operation(summary = "Create a new question", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Question created successfully", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "400", description = "Invalid question data")})
    @PostMapping
    public QuestionDTO createQuestion(@Valid @RequestBody QuestionDTO questionDTO) {
        return questionService.createQuestion(questionDTO);
    }

    @Operation(summary = "Update an existing question", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question updated successfully",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "400", description = "Invalid question data"),
            @ApiResponse(responseCode = "404", description = "Question not found")})
    @PutMapping
    public QuestionDTO updateQuestion(@Valid @RequestBody QuestionDTO questionDTO) throws RecordNotFoundException {
        return questionService.updateQuestion(questionDTO);
    }

    @Operation(summary = "Delete a question by ID", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "404", description = "Question not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @Parameter(description = "The ID of the question to delete", example = "123")
            @PathVariable Long id) throws RecordNotFoundException {
        questionService.deleteQuestionById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
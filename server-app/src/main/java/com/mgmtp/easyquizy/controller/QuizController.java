package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.dto.EventDTO;
import com.mgmtp.easyquizy.dto.QuizDTO;
import com.mgmtp.easyquizy.dto.QuizDtoDetail;
import com.mgmtp.easyquizy.service.QuizServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@Tag(name = "Quiz")
@RestController
@RequiredArgsConstructor
@RequestMapping("/quizzes")
public class QuizController {
    private final QuizServiceImpl quizService;

    @Value("${easy-quizy.api.default-page-size}")
    private int defaultPageSize;

    /**
     * Create a new quiz
     *
     * @param quiz information for insert
     * @return Newly created quiz
     */
    @Operation(summary = "Create a new quiz", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create a new quiz successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = QuizDtoDetail.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @PostMapping
    public QuizDtoDetail createQuiz(
            @Parameter(description = "Quiz's information")
            @Valid @RequestBody QuizDtoDetail quiz) {
        return quizService.createQuiz(quiz);
    }

    /**
     * Retrieve all quiz data from an event
     *
     * @param eventId the event's id of all the quizzes
     * @param offset  the offset of the first result to return
     * @param limit   the maximum number of results to return
     * @param keyword the keyword to search for
     * @return List all the quizzes from an event
     */
    @Operation(summary = "Get all quiz from an event with paging, filtering (if needed)", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found quizzes",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = QuizDtoDetail.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @GetMapping
    public Page<QuizDTO> getAllQuizOfEvent(
            @Parameter(description = "The event's id")
            @RequestParam(name = "eventId") Long eventId,
            @Parameter(description = "The offset of the first result to return")
            @RequestParam(name = "offset", defaultValue = "0")
            @Min(value = 0, message = "Offset must be greater than or equal to 0") int offset,
            @Parameter(description = "The maximum number of results to return")
            @RequestParam(name = "limit", required = false) Integer limit,
            @Parameter(description = "The keyword to search for")
            @RequestParam(name = "keyword", required = false) String keyword) {
        if (limit == null) {
            limit = defaultPageSize;
        }
        return quizService.getAllQuizOfEvent(eventId, keyword, offset, limit);
    }

    /**
     * Retrieve single quiz information by id
     *
     * @param id quiz's id
     * @return quiz's information by id
     */
    @Operation(summary = "Get an quiz by its id", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = QuizDtoDetail.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @GetMapping("/{id}")
    public QuizDtoDetail getQuizById(@PathVariable("id") Long id) {
        return quizService.getQuizById(id);
    }

    /**
     * Update an quiz's information
     *
     * @param quiz information for update a quiz
     * @return Updated quiz
     */
    @Operation(summary = "Update an quiz", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = QuizDtoDetail.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @PutMapping
    public QuizDtoDetail updateQuiz(
            @Parameter(description = "Quiz's information")
            @Valid @RequestBody QuizDtoDetail quiz) {
        return quizService.updateQuiz(quiz);
    }

    /**
     * Delete an quiz by id
     *
     * @param id Quiz's id
     * @return String "DELETED"
     */
    @Operation(summary = "Delete an quiz by its id", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz deleted",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @DeleteMapping("/{id}")
    public String deleteQuizById(
            @PathVariable("id") Long id) {
        quizService.deleteQuizById(id);
        return "DELETED";
    }
}

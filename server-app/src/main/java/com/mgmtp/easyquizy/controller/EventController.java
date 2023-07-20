package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.dto.event.EventDTO;
import com.mgmtp.easyquizy.service.EventServiceImpl;
import com.mgmtp.easyquizy.validator.DateRangeValidator;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Tag(name = "Event")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
@Validated
public class EventController {

    private final EventServiceImpl eventService;

    private final DateRangeValidator dateRangeValidator;

    @Value("${easy-quizy.api.default-page-size}")
    private int defaultPageSize;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(dateRangeValidator);
    }

    /**
     * Create event information
     *
     * @param event information for insert
     * @return Http Status
     */
    @Operation(summary = "Create a new event", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create a new event successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @PostMapping
    public EventDTO createEvent(
            @Parameter(description = "Event's information")
            @Valid @RequestBody EventDTO event) {
        return eventService.createEvent(event);
    }

    /**
     * Retrive all event data
     *
     * @param offset  the offset of the first result to return
     * @param limit   the maximum number of results to return
     * @param keyword the keyword to search for
     * @return List all of event
     */
    @Operation(summary = "Get all events with paging, filtering (if needed)", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found events",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @GetMapping
    public Page<EventDTO> getAllEvent(
            @Parameter(description = "The offset of the first result to return")
            @RequestParam(name = "offset", defaultValue = "0")
            @Min(value = 0, message = "Offset must be greater than or equal to 0") int offset,
            @Parameter(description = "The maximum number of results to return")
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "year", required = false) Integer year,
            @Parameter(description = "The keyword to search for")
            @RequestParam(name = "keyword", required = false) String keyword) {
        if (limit == null) {
            limit = defaultPageSize;
        }
        return eventService.getAllEvent(keyword, year , offset, limit);
    }

    @Operation(summary = "Get all years", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all years",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @GetMapping("/years")
    public List<Integer> getAllYear() {
        return eventService.getAllYear();
    }

    /**
     * Retrieve single event information by id
     *
     * @param id Event id
     * @return event's information by id
     */
    @Operation(summary = "Get an event by its id", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable("id") Long id) {
        return eventService.getEventById(id);
    }

    /**
     * Update an event's information
     *
     * @param event information for update an event
     * @return Http Status
     */
    @Operation(summary = "Update an event", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @PutMapping
    public EventDTO updateEvent(
            @Valid @RequestBody EventDTO event) {
        return eventService.updateEvent(event);
    }

    /**
     * Delete an event by id
     *
     * @param id Event's id
     * @return String "DELETED"
     */
    @Operation(summary = "Delete an event by its id", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event deleted",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @DeleteMapping("/{id}")
    public String deleteEventById(
            @PathVariable("id") Long id) {
        eventService.deleteEventById(id);
        return "DELETED";
    }
}
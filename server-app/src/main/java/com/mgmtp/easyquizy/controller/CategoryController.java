package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.dto.CategoryDTO;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.model.category.CategoryEntity;
import com.mgmtp.easyquizy.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Category")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Get all categories", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all categories",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail")
    })
    @GetMapping
    public List<CategoryEntity> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Operation(summary = "Create a new category", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "400", description = "Invalid category data")})
    @PostMapping
    public CategoryEntity createCategory(@Valid @RequestBody CategoryDTO categoryDTO) throws InvalidFieldsException {
        return categoryService.createCategory(categoryDTO);
    }

    @Operation(summary = "Update an existing category", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "400", description = "Invalid category data"),
            @ApiResponse(responseCode = "404", description = "Category not found")})
    @PutMapping
    public CategoryEntity updateCategory(@Valid @RequestBody CategoryDTO categoryDTO) throws RecordNotFoundException, InvalidFieldsException {
        return categoryService.updateCategory(categoryDTO);
    }

    @Operation(summary = "Delete a category by ID", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Authentication fail"),
            @ApiResponse(responseCode = "401", description = "Authorization fail"),
            @ApiResponse(responseCode = "404", description = "Category not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "The ID of the category to delete", example = "1")
            @PathVariable Long id) throws RecordNotFoundException {
        categoryService.deleteCategoryById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

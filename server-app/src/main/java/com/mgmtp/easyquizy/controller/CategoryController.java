package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.model.category.CategoryEntity;
import com.mgmtp.easyquizy.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all categories",
                    content = {@Content(mediaType = "application/json")})
    })
    @GetMapping
    public List<CategoryEntity> getAllCategories(
    ) {
        return categoryService.getAllCategories();
    }
}

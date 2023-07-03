package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.CategoryDTO;
import com.mgmtp.easyquizy.model.category.CategoryEntity;

import java.util.List;

public interface CategoryService {
    List<CategoryEntity> getAllCategories();

    CategoryEntity createCategory(CategoryDTO categoryDTO);

    CategoryEntity updateCategory(CategoryDTO categoryDTO);

    void deleteCategoryById(Long id);
}

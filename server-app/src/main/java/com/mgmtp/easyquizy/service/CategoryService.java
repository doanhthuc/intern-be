package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.CategoryInfoDTO;
import com.mgmtp.easyquizy.model.category.CategoryEntity;

import java.util.List;

public interface CategoryService {
    List<CategoryEntity> getAllCategories();

    CategoryEntity createCategory(CategoryInfoDTO categoryInfoDTO);

    CategoryEntity updateCategory(CategoryInfoDTO categoryInfoDTO);

    void deleteCategoryById(Long id);
}

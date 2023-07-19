package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.category.CategoryInfoDTO;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.mapper.CategoryMapper;
import com.mgmtp.easyquizy.model.category.CategoryEntity;
import com.mgmtp.easyquizy.repository.CategoryRepository;
import com.mgmtp.easyquizy.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public CategoryEntity createCategory(CategoryInfoDTO categoryInfoDTO) {
        try {
            categoryInfoDTO.setId(null);
            return categoryRepository.save(categoryMapper.categoryInfoDtoToCategoryEntity(categoryInfoDTO));
        } catch (DataIntegrityViolationException e) {
            throw InvalidFieldsException.fromFieldError("categoryName", "Category exists!");
        }
    }

    @Override
    @Transactional
    public CategoryEntity updateCategory(CategoryInfoDTO categoryInfoDTO) {
        try {
            if (categoryInfoDTO.getId() == null) {
                throw InvalidFieldsException.fromFieldError("categoryId", "ID is required");
            }
            if (!categoryRepository.existsById(categoryInfoDTO.getId())) {
                throw new RecordNotFoundException("Category not found");
            }
            return categoryRepository.save(categoryMapper.categoryInfoDtoToCategoryEntity(categoryInfoDTO));
        } catch (DataIntegrityViolationException e) {
            throw InvalidFieldsException.fromFieldError("categoryName", "Category name exists");
        }
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RecordNotFoundException("Category not found");
        }
        if (questionRepository.existsByCategoryId(id)) {
            throw InvalidFieldsException.fromFieldError("categoryId", "A question exists in this category");
        }
        categoryRepository.deleteById(id);
    }
}

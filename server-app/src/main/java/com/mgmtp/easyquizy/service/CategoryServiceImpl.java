package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.CategoryDTO;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.mapper.CategoryMapper;
import com.mgmtp.easyquizy.model.category.CategoryEntity;
import com.mgmtp.easyquizy.repository.CategoryRepository;
import com.mgmtp.easyquizy.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public CategoryEntity createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw  InvalidFieldsException.fromFieldError("categoryName", "Category is existed");
        }
        return categoryRepository.save(categoryMapper.categoryDtoToCategoryEntity(categoryDTO));
    }

    @Override
    public CategoryEntity updateCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getId() == null) {
            throw InvalidFieldsException.fromFieldError("categoryId", "ID is required");
        }
        if (!categoryRepository.existsById(categoryDTO.getId())) {
            throw new RecordNotFoundException("Not found category");
        }
        return categoryRepository.save(categoryMapper.categoryDtoToCategoryEntity(categoryDTO));
    }

    @Override
    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RecordNotFoundException("Not found category");
        }
        if (questionRepository.existsByCategoryId(id)){
            throw InvalidFieldsException.fromFieldError("categoryId", "Exist question by this category");
        }
        categoryRepository.deleteById(id);
    }
}

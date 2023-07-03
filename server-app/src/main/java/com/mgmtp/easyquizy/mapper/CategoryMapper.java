package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.CategoryDTO;
import com.mgmtp.easyquizy.model.category.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity categoryDtoToCategoryEntity(CategoryDTO categoryDTO);
}

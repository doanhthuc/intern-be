package com.mgmtp.easyquizy.mapper;

import com.mgmtp.easyquizy.dto.CategoryInfoDTO;
import com.mgmtp.easyquizy.model.category.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity categoryInfoDtoToCategoryEntity(CategoryInfoDTO categoryInfoDTO);
}

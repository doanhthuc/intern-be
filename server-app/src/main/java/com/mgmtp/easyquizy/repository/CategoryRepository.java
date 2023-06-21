package com.mgmtp.easyquizy.repository;

import com.mgmtp.easyquizy.model.category.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}

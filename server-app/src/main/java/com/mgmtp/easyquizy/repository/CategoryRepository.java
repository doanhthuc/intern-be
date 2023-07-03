package com.mgmtp.easyquizy.repository;

import com.mgmtp.easyquizy.model.category.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByName(String categoryName);

    boolean existsByName(String categoryName);
}

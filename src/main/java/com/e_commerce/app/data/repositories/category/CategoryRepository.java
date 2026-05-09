package com.e_commerce.app.data.repositories.category;


import com.e_commerce.app.data.entities.category.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByParentIsNull();         // top-level categories
    List<CategoryEntity> findByParentId(Long parentId); // subcategories
    Optional<CategoryEntity> findByName(String name);
}
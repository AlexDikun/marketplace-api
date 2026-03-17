package io.github.alexdikun.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.alexdikun.marketplace.entities.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    boolean existsByNameAndIdNot(String name, Long id);

    @EntityGraph(attributePaths = {"parentCategory"})
    Page<CategoryEntity> findAll(Pageable pageable);

}

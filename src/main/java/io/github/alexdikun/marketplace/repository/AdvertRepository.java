package io.github.alexdikun.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.alexdikun.marketplace.entities.AdvertEntity;
import java.util.Optional;


@Repository
public interface AdvertRepository extends JpaRepository<AdvertEntity, Long> {

    @EntityGraph(attributePaths = {"user", "category"})
    @Query("""
        SELECT a
        FROM AdvertEntity a
        WHERE
        LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))
        OR
        LOWER(a.description) LIKE LOWER(CONCAT('%', :query, '%')) 
    """)
    Page<AdvertEntity> search(@Param("query") String query, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "category", "images"})
    Optional<AdvertEntity> findWithDetailsById(Long id);
    
}

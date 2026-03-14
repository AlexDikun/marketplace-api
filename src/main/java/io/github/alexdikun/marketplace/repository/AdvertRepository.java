package io.github.alexdikun.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.alexdikun.marketplace.entities.AdvertEntity;

@Repository
public interface AdvertRepository extends JpaRepository<AdvertEntity, Long> {

    @Query("""
        SELECT a
        FROM AdvertEntity a
        WHERE
        LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))
        OR
        LOWER(a.description) LIKE LOWER(CONCAT('%', :query, '%')) 
    """)
    List<AdvertEntity> search(@Param("query") String query);
    
}

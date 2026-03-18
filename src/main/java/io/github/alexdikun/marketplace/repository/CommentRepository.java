package io.github.alexdikun.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.alexdikun.marketplace.entities.CommentEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    
    @EntityGraph(attributePaths = {"user", "parentComment"})
    Page<CommentEntity> findByAdvertId(Long advertId, Pageable pageable);

}

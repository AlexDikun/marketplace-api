package io.github.alexdikun.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.alexdikun.marketplace.entities.AdvertEntity;

@Repository
public interface AdvertRepository extends JpaRepository<AdvertEntity, Long> {}

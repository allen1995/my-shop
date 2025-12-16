package com.myshop.repository;

import com.myshop.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    Page<Work> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    @Query("SELECT w FROM Work w WHERE w.userId = :userId AND (:category IS NULL OR w.category = :category)")
    Page<Work> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category, Pageable pageable);
    
    @Query("SELECT w FROM Work w WHERE w.userId = :userId AND w.tags LIKE %:tag%")
    Page<Work> findByUserIdAndTag(@Param("userId") Long userId, @Param("tag") String tag, Pageable pageable);
    
    Page<Work> findByUserIdAndIsFavoriteOrderByCreateTimeDesc(Long userId, Boolean isFavorite, Pageable pageable);
    
    boolean existsByIdAndUserId(Long id, Long userId);
    
    long countByUserId(Long userId);
    
    long countByUserIdAndIsFavorite(Long userId, Boolean isFavorite);
}



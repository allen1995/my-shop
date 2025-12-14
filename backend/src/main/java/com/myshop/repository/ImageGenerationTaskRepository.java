package com.myshop.repository;

import com.myshop.entity.ImageGenerationTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageGenerationTaskRepository extends JpaRepository<ImageGenerationTask, Long> {
    List<ImageGenerationTask> findByUserIdOrderByCreateTimeDesc(Long userId);
    Optional<ImageGenerationTask> findByIdAndUserId(Long id, Long userId);
}



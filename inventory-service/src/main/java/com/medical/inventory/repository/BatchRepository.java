package com.medical.inventory.repository;

import com.medical.inventory.entity.Batch;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    List<Batch> findByProductId(Long productId);

    List<Batch> findByProductIdAndQuantityGreaterThanAndExpiryDateAfterOrderByExpiryDateAsc(
            Long productId,
            int quantity,
            LocalDate date
    );
}

package com.medical.inventory.service;

import com.medical.inventory.client.AuditClient;
import com.medical.inventory.dto.BatchRequest;
import com.medical.inventory.entity.Batch;
import com.medical.inventory.entity.Location;
import com.medical.inventory.entity.Product;
import com.medical.inventory.entity.StockMovement;
import com.medical.inventory.enums.MovementType;
import com.medical.inventory.exception.ResourceNotFoundException;
import com.medical.inventory.repository.BatchRepository;
import com.medical.inventory.repository.StockMovementRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final ProductService productService;
    private final LocationService locationService;
    private final StockMovementRepository stockMovementRepository;
    private final AuditClient auditClient;

    public Batch createBatch(BatchRequest request) {
        Product product = productService.getProductById(request.getProductId());
        Location location = locationService.getLocationById(request.getLocationId());

        Batch batch = Batch.builder()
                .batchNumber(generateBatchNumber(request))
                .batchName(request.getBatchName())
                .quantity(request.getQuantity())
                .expiryDate(request.getExpiryDate())
                .product(product)
                .location(location)
                .build();

        Batch savedBatch = batchRepository.save(batch);

        StockMovement movement = StockMovement.builder()
                .productId(product.getId())
                .batchId(savedBatch.getId())
                .movementType(MovementType.IN)
                .quantity(request.getQuantity())
                .reason("New batch received")
                .build();

        stockMovementRepository.save(movement);
        auditClient.log("system", "STOCK_IN", "Batch", savedBatch.getId(), "Batch received: " + savedBatch.getBatchNumber());

        return savedBatch;
    }

    private String generateBatchNumber(BatchRequest request) {
        if (request.getBatchNumber() != null && !request.getBatchNumber().isBlank()) {
            return request.getBatchNumber();
        }

        return "BATCH-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    public List<Batch> getBatchesByProduct(Long productId) {
        return batchRepository.findByProductId(productId);
    }

    public void deleteBatch(Long id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + id));

        batchRepository.delete(batch);
        auditClient.log("system", "DELETE", "Batch", id, "Batch removed: " + batch.getBatchNumber());
    }
}

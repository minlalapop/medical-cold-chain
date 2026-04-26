package com.medical.inventory.service;

import com.medical.inventory.client.AuditClient;
import com.medical.inventory.dto.ConsumeStockRequest;
import com.medical.inventory.dto.StockResponse;
import com.medical.inventory.entity.Batch;
import com.medical.inventory.entity.Product;
import com.medical.inventory.entity.StockMovement;
import com.medical.inventory.enums.MovementType;
import com.medical.inventory.exception.InsufficientStockException;
import com.medical.inventory.repository.BatchRepository;
import com.medical.inventory.repository.ProductRepository;
import com.medical.inventory.repository.StockMovementRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;
    private final StockMovementRepository stockMovementRepository;
    private final AuditClient auditClient;

    public int getTotalStock(Long productId) {
        return batchRepository
                .findByProductIdAndQuantityGreaterThanAndExpiryDateAfterOrderByExpiryDateAsc(
                        productId,
                        0,
                        LocalDate.now()
                )
                .stream()
                .mapToInt(Batch::getQuantity)
                .sum();
    }

    public boolean checkStock(Long productId, int requestedQuantity) {
        return getTotalStock(productId) >= requestedQuantity;
    }

    public StockResponse getStockByProduct(Long productId) {
        Product product = productService.getProductById(productId);
        int totalStock = getTotalStock(productId);

        return new StockResponse(
                product.getId(),
                product.getName(),
                totalStock,
                product.getMinStockThreshold(),
                totalStock <= product.getMinStockThreshold()
        );
    }

    @Transactional
    public void consumeStock(ConsumeStockRequest request) {
        Product product = productService.getProductById(request.getProductId());

        List<Batch> batches = batchRepository
                .findByProductIdAndQuantityGreaterThanAndExpiryDateAfterOrderByExpiryDateAsc(
                        product.getId(),
                        0,
                        LocalDate.now()
                );

        int remaining = request.getQuantity();

        for (Batch batch : batches) {
            if (remaining == 0) {
                break;
            }

            int available = batch.getQuantity();

            if (available >= remaining) {
                batch.setQuantity(available - remaining);
                batchRepository.save(batch);

                saveMovement(product.getId(), batch.getId(), MovementType.OUT, remaining, request.getReason());
                remaining = 0;
            } else {
                batch.setQuantity(0);
                batchRepository.save(batch);

                saveMovement(product.getId(), batch.getId(), MovementType.OUT, available, request.getReason());
                remaining -= available;
            }
        }

        if (remaining > 0) {
            throw new InsufficientStockException(
                    "No usable stock for " + product.getName()
                            + ". The approved request cannot be delivered because available batches are expired, expiring today, or empty."
            );
        }

        auditClient.log("system", "STOCK_OUT", "Product", product.getId(), "Stock consumed: " + request.getQuantity());
    }

    public List<StockResponse> getLowStockProducts() {
        List<StockResponse> result = new ArrayList<>();

        for (Product product : productRepository.findAll()) {
            int totalStock = getTotalStock(product.getId());

            if (totalStock <= product.getMinStockThreshold()) {
                result.add(new StockResponse(
                        product.getId(),
                        product.getName(),
                        totalStock,
                        product.getMinStockThreshold(),
                        true
                ));
            }
        }

        return result;
    }

    private void saveMovement(Long productId, Long batchId, MovementType type, int quantity, String reason) {
        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .batchId(batchId)
                .movementType(type)
                .quantity(quantity)
                .reason(reason)
                .build();

        stockMovementRepository.save(movement);
    }
}

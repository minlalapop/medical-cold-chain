package com.medical.inventory.controller;

import com.medical.inventory.dto.ConsumeStockRequest;
import com.medical.inventory.dto.StockResponse;
import com.medical.inventory.service.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/stock/{productId}")
    public StockResponse getStockByProduct(@PathVariable Long productId) {
        return inventoryService.getStockByProduct(productId);
    }

    @GetMapping("/check")
    public boolean checkStock(@RequestParam Long productId, @RequestParam int quantity) {
        return inventoryService.checkStock(productId, quantity);
    }

    @PostMapping("/consume")
    public void consumeStock(@Valid @RequestBody ConsumeStockRequest request) {
        inventoryService.consumeStock(request);
    }

    @GetMapping("/low-stock")
    public List<StockResponse> getLowStockProducts() {
        return inventoryService.getLowStockProducts();
    }
}

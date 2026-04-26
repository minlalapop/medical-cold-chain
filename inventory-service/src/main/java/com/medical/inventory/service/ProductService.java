package com.medical.inventory.service;

import com.medical.inventory.client.AuditClient;
import com.medical.inventory.dto.ProductRequest;
import com.medical.inventory.entity.Product;
import com.medical.inventory.exception.ResourceNotFoundException;
import com.medical.inventory.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AuditClient auditClient;

    public Product createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .requiresColdChain(request.isRequiresColdChain())
                .minStockThreshold(request.getMinStockThreshold())
                .build();

        Product savedProduct = productRepository.save(product);
        auditClient.log("system", "CREATE", "Product", savedProduct.getId(), "Product created: " + savedProduct.getName());
        return savedProduct;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product updateProduct(Long id, ProductRequest request) {
        Product product = getProductById(id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setRequiresColdChain(request.isRequiresColdChain());
        product.setMinStockThreshold(request.getMinStockThreshold());

        Product savedProduct = productRepository.save(product);
        auditClient.log("system", "UPDATE", "Product", savedProduct.getId(), "Product updated: " + savedProduct.getName());
        return savedProduct;
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
        auditClient.log("system", "DELETE", "Product", id, "Product deleted: " + product.getName());
    }
}

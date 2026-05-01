package com.pastrypoint.controller;

import com.pastrypoint.model.Product;
import com.pastrypoint.repository.ProductRepository;
import com.pastrypoint.repository.OrderRepository;
import com.pastrypoint.service.DemoDataSeeder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final DemoDataSeeder demoDataSeeder;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        product.setInStock(true);
        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setCategory(productDetails.getCategory());
        product.setImageUrl(productDetails.getImageUrl());
        product.setDescription(productDetails.getDescription());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setInStock(productDetails.isInStock());
        return ResponseEntity.ok(productRepository.save(product));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Product> toggleStock(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setInStock(!product.isInStock());
        return ResponseEntity.ok(productRepository.save(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping("/reset-demo")
    public ResponseEntity<Void> resetDemoCatalog() {
        orderRepository.deleteAll();
        demoDataSeeder.reseedProducts();
        return ResponseEntity.ok().build();
    }
}

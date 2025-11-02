package org.example.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.request.ProductRequest;
import org.example.ecommerce.dto.response.ProductResponse;
import org.example.ecommerce.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @GetMapping("products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/admin/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/products/category/{categoryName}")
    public ResponseEntity<List<ProductResponse>> getAllProductsByCategory(@PathVariable String categoryName) {
        return ResponseEntity.ok(productService.getProductByCategory(categoryName));
    }

    @GetMapping("/products/available")
    public ResponseEntity<List<ProductResponse>> getAllAvailableProducts() {
        return ResponseEntity.ok(productService.getAvailableProducts());
    }

    @PostMapping("/admin/products")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PutMapping("/admin/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PostMapping("/{id}")
    public ResponseEntity<ProductResponse> updateStock(@PathVariable Long id, @RequestBody Integer request) {
        return ResponseEntity.ok(productService.updateStock(id, request));
    }

    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

}

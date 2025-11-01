package org.example.ecommerce.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.request.ProductRequest;
import org.example.ecommerce.entity.Category;
import org.example.ecommerce.entity.Product;
import org.example.ecommerce.repository.CategoryRepository;
import org.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByIsAvailableTrue().orElseThrow();
    }

    public List<Product> getProductByCategory(String categoryName) {
        if (!categoryRepository.existsByName(categoryName)) throw new RuntimeException("Category not found");
        return productRepository.findByCategoryName(categoryName);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Product createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        product.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : false);
        return productRepository.save(product);

    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id).orElseThrow();

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        product.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : false);
        return productRepository.save(product);

    }

    public Product updateStock(Long id, Integer stockQuantity) {
        Product product = productRepository.findById(id).orElseThrow();

        int newStockQuantity = product.getStockQuantity() + stockQuantity;
        if (newStockQuantity < 0) throw new RuntimeException("stock quantity cannot be less than 0");

        product.setStockQuantity(newStockQuantity);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (productRepository.findById(id).isEmpty()) throw new RuntimeException("Product not found");
        productRepository.deleteById(id);
    }


}

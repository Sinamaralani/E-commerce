package org.example.ecommerce.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.request.ProductRequest;
import org.example.ecommerce.dto.response.ProductResponse;
import org.example.ecommerce.entity.Category;
import org.example.ecommerce.entity.Product;
import org.example.ecommerce.exception.BadRequestException;
import org.example.ecommerce.exception.ResourceNotFoundException;
import org.example.ecommerce.repository.CategoryRepository;
import org.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toProductResponse(product);
    }

    public List<ProductResponse> getAvailableProducts() {
        return productRepository.findByIsAvailableTrue()
                .stream().map(this::toProductResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> getProductByCategory(String categoryName) {
        if (!categoryRepository.existsByName(categoryName))
            throw new ResourceNotFoundException("Category not found with name: " + categoryName);
        return productRepository.findByCategoryName(categoryName)
                .stream().map(this::toProductResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream().map(this::toProductResponse).collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = new Product();
        return helper(product, category, request);

    }


    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        return helper(product, category, request);

    }

    public ProductResponse updateStock(Long id, Integer stockQuantity) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        int newStockQuantity = product.getStockQuantity() + stockQuantity;
        if (newStockQuantity < 0) throw new BadRequestException("stock quantity cannot be less than 0");

        product.setStockQuantity(newStockQuantity);
        return toProductResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        if (productRepository.findById(id).isEmpty())
            throw new ResourceNotFoundException("Product not found with id: " + id);
        productRepository.deleteById(id);
    }

    private ProductResponse helper(Product product, Category category, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        product.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : false);
        return toProductResponse(productRepository.save(product));
    }

    private ProductResponse toProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setImageUrl(product.getImageUrl());
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());
        response.setIsAvailable(product.getIsAvailable());
        return response;
    }

}

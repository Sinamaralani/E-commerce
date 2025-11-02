package org.example.ecommerce.repository;

import org.example.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsAvailableTrue();

    List<Product> findByCategoryName(String categoryName);
}
